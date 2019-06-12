package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.inject.BatchSize;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiPassword;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUrl;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUser;
import gov.ca.cwds.jobs.cap.users.job.CapUsersIncrementalJob;
import gov.ca.cwds.jobs.cap.users.job.CapUsersInitialJob;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import gov.ca.cwds.jobs.cap.users.service.CapUsersSavePointContainerService;
import gov.ca.cwds.jobs.cap.users.service.CapUsersSavePointService;
import gov.ca.cwds.jobs.cap.users.service.CwsChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.CwsChangedUsersServiceImpl;
import gov.ca.cwds.jobs.cap.users.service.IdmService;
import gov.ca.cwds.jobs.cap.users.service.IdmServiceImpl;
import gov.ca.cwds.jobs.cap.users.service.PerfTestCwsChangedUsersService;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchAliasFinalizer;
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.ElasticsearchBulkSize;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.IndexAwareSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersJobModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(CapUsersJobModule.class);

  private Class<? extends BulkWriter<ChangedUserDto>> capElasticWriterClass;
  private Class<? extends IdmService> idmService;
  private Class<? extends JobModeFinalizer> jobModeFinalizerClass;
  private CapUsersJobConfiguration configuration;
  private JobMode jobMode;

  public CapUsersJobModule(CapUsersJobConfiguration jobConfiguration, JobMode jobMode) {
    this.configuration = jobConfiguration;
    this.jobMode = jobMode;
    this.capElasticWriterClass = CapUsersWriter.class;
    this.jobModeFinalizerClass = ElasticsearchAliasFinalizer.class;
    this.idmService = IdmServiceImpl.class;
  }

  public CapUsersJobConfiguration getConfiguration() {
    return configuration;
  }

  public void setCapElasticWriterClass(
      Class<? extends BulkWriter<ChangedUserDto>> capUsersElasticWriterClass) {
    this.capElasticWriterClass = capUsersElasticWriterClass;
  }

  public void setJobModeFinalizerClass(
      Class<? extends JobModeFinalizer> jobModeFinalizerClass) {
    this.jobModeFinalizerClass = jobModeFinalizerClass;
  }

  public void setIdmService(Class<? extends IdmService> idmService) {
    this.idmService = idmService;
  }

  @Override
  protected void configure() {
    configureJobModes();
    bind(new TypeLiteral<BulkWriter<ChangedUserDto>>() {
    }).to(capElasticWriterClass);
    bindConstant().annotatedWith(ElasticsearchBulkSize.class)
        .to(configuration.getElasticSearchBulkSize());
    bindConstant().annotatedWith(PerryApiUrl.class)
        .to(getConfiguration().getPerryApiUrl());
    bindConstant().annotatedWith(BatchSize.class)
        .to(getConfiguration().getBatchSize());
    bindConstant().annotatedWith(PerryApiUser.class)
        .to(getJobsConfiguration().getPerryApiUser());
    bindConstant().annotatedWith(PerryApiPassword.class)
        .to(getJobsConfiguration().getPerryApiPassword());
    bind(IdmService.class).to(idmService);
    bind(new TypeLiteral<SavePointContainerService<CapUsersSavePoint>>() {
    }).annotatedWith(BaseContainerService.class).to(CapUsersSavePointContainerService.class);
    bind(new TypeLiteral<SavePointContainerService<CapUsersSavePoint>>() {
    }).annotatedWith(PrimaryContainerService.class).to(SavePointContainerServiceDecorator.class);
    bind(new TypeLiteral<SavePointService<CapUsersSavePoint>>() {
    }).toProvider(CapUsersSavePointServiceProvider.class);
    bind(CapUsersSavePointService.class).toProvider(CapUsersSavePointServiceProvider.class);
    install(new CwsCmsDataAccessModule(getJobsConfiguration().getCmsDataSourceFactory()));
  }

  private void configureJobModes() {
    switch (jobMode) {
      case INITIAL_LOAD:
      case INITIAL_RESUME:
        configureInitialMode();
        break;
      case INCREMENTAL_LOAD:
        configureIncrementalMode();
        break;
      default:
        String errorMsg = "Job mode cannot be defined";
        LOG.info(errorMsg);
        throw new UnsupportedOperationException(errorMsg);
    }
  }

  private void configureIncrementalMode() {
    bind(Job.class).to(CapUsersIncrementalJob.class);
    if (getJobsConfiguration().isPerformanceTestMode()) {
      bind(CwsChangedUsersService.class).toProvider(PerfTestCwsChangedUsersServiceProvider.class);
    } else {
      bind(CwsChangedUsersService.class).toProvider(CwsChangedUsersServiceProvider.class);
    }
  }

  private void configureInitialMode() {
    bind(Job.class).to(CapUsersInitialJob.class);
    bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).to(jobModeFinalizerClass);
    bind(JobModeFinalizer.class).annotatedWith(SecondaryFinalizer.class).toInstance(() -> {});
    bind(CwsChangedUsersService.class).toProvider(CwsChangedUsersServiceProvider.class);
  }

  @Provides
  protected CapUsersJobConfiguration getJobsConfiguration() {
    return getConfiguration();
  }

  @Provides
  public Client provideClient() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
        .property(ClientProperties.CONNECT_TIMEOUT,
            getConfiguration().getJerseyClientConnectTimeout())
        .property(ClientProperties.READ_TIMEOUT,
            getJobsConfiguration().getJerseyClientReadTimeout())
        // Just ignore host verification, client will call trusted resources only
        .hostnameVerifier((hostName, sslSession) -> true);
    Client client = clientBuilder.build();
    client.register(new JacksonJsonProvider(mapper));
    return client;
  }

  static class CwsChangedUsersServiceProvider implements Provider<CwsChangedUsersServiceImpl> {

    @Inject
    private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

    @Inject
    private Injector injector;

    @Override
    public CwsChangedUsersServiceImpl get() {
      CwsChangedUsersServiceImpl service = this.unitOfWorkAwareProxyFactory
          .create(CwsChangedUsersServiceImpl.class);
      this.injector.injectMembers(service);
      return service;
    }

  }

  static class PerfTestCwsChangedUsersServiceProvider implements
      Provider<PerfTestCwsChangedUsersService> {

    @Inject
    private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

    @Inject
    private Injector injector;

    @Override
    public PerfTestCwsChangedUsersService get() {
      PerfTestCwsChangedUsersService service = this.unitOfWorkAwareProxyFactory
          .create(PerfTestCwsChangedUsersService.class);
      this.injector.injectMembers(service);
      return service;
    }

  }

  static class CapUsersSavePointServiceProvider implements Provider<CapUsersSavePointService> {

    @Inject
    private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

    @Inject
    private Injector injector;

    @Override
    public CapUsersSavePointService get() {
      CapUsersSavePointService service = this.unitOfWorkAwareProxyFactory
          .create(CapUsersSavePointService.class);
      this.injector.injectMembers(service);
      return service;
    }

  }

  static class SavePointContainerServiceDecorator extends
      IndexAwareSavePointContainerService<CapUsersSavePoint> {  }


}

