package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiPassword;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUrl;
import gov.ca.cwds.jobs.cap.users.inject.PerryApiUser;
import gov.ca.cwds.jobs.cap.users.job.CapUsersIncrementalJob;
import gov.ca.cwds.jobs.cap.users.job.CapUsersInitialJob;
import gov.ca.cwds.jobs.cap.users.service.IdmService;
import gov.ca.cwds.jobs.cap.users.service.IdmServiceImpl;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeDefaultJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import java.time.LocalDateTime;

public class CapUsersJobModule extends AbstractBaseJobModule {
  private static final Logger LOG = LoggerFactory.getLogger(CapUsersJobModule.class);

  private Class<? extends BulkWriter<ChangedUserDto>> capElasticWriterClass;

  private Class<? extends IdmService> idmService;

  public CapUsersJobModule(String[] args) {
    super(args);
    this.capElasticWriterClass = CapUsersWriter.class;
    this.idmService = IdmServiceImpl.class;
  }

  public void setCapElasticWriterClass(
          Class<? extends BulkWriter<ChangedUserDto>> capUsersElasticWriterClass) {
    this.capElasticWriterClass = capUsersElasticWriterClass;
  }

  public void setIdmService(Class<? extends IdmService> idmService) {
    this.idmService = idmService;
  }

  @Override
  protected void configure() {
    super.configure();
    configureJobModes();
    bind(new TypeLiteral<BulkWriter<ChangedUserDto>>() {
    }).to(capElasticWriterClass);
    bindConstant().annotatedWith(PerryApiUrl.class)
            .to(getJobsConfiguration(getJobOptions()).getPerryApiUrl());
    bindConstant().annotatedWith(PerryApiUser.class)
            .to(getJobsConfiguration(getJobOptions()).getPerryApiUser());
    bindConstant().annotatedWith(PerryApiPassword.class)
            .to(getJobsConfiguration(getJobOptions()).getPerryApiPassword());
    bind(IdmService.class).to(idmService);
    bind(
            new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
            }).to(LocalDateTimeSavePointContainerService.class);
  }

  private void configureJobModes() {
    switch (defineJobMode()) {
      case INITIAL_LOAD:
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
    install(new CwsCmsDataAccessModule());
  }

  private void configureInitialMode() {
    bind(Job.class).to(CapUsersInitialJob.class);
  }

  private DefaultJobMode defineJobMode() {
    LocalDateTimeDefaultJobModeService timestampDefaultJobModeService =
            new LocalDateTimeDefaultJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
            new LocalDateTimeSavePointContainerService(getJobOptions().getLastRunLoc());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);

    return timestampDefaultJobModeService.getCurrentJobMode();
  }


  @Provides
  @Override
  @Inject
  protected CapUsersJobConfiguration getJobsConfiguration(JobOptions jobsOptions) {
    CapUsersJobConfiguration capUsersJobConfiguration = BaseJobConfiguration.getJobsConfiguration(CapUsersJobConfiguration.class, jobsOptions.getEsConfigLoc());
    capUsersJobConfiguration.setIndexSettings("cap.users.settings.json");
    capUsersJobConfiguration.setDocumentMapping("cap.users.mapping.json");
    return capUsersJobConfiguration;

  }

  @Provides
  public Client provideClient() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
            .property(ClientProperties.CONNECT_TIMEOUT, getJobsConfiguration(getJobOptions()).getJerseyClientConnectTimeout())
            .property(ClientProperties.READ_TIMEOUT, getJobsConfiguration(getJobOptions()).getJerseyClientReadTimeout())
            // Just ignore host verification, client will call trusted resources only
            .hostnameVerifier((hostName, sslSession) -> true);
    Client client = clientBuilder.build();
    client.register(new JacksonJsonProvider(mapper));
    return client;
  }
}

