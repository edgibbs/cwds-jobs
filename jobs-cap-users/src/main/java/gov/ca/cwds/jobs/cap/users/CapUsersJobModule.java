package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
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
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.inject.ElasticsearchBulkSize;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeDefaultJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersJobModule extends AbstractModule {

  private static final Logger LOG = LoggerFactory.getLogger(CapUsersJobModule.class);

  private Class<? extends BulkWriter<ChangedUserDto>> capElasticWriterClass;
  private Class<? extends IdmService> idmService;
  private CapUsersJobConfiguration configuration;
  private String lastRunLoc;

  public CapUsersJobModule(CapUsersJobConfiguration jobConfiguration, String lastRunLoc) {
    this.configuration = jobConfiguration;
    this.lastRunLoc = lastRunLoc;
    this.capElasticWriterClass = CapUsersWriter.class;
    this.idmService = IdmServiceImpl.class;
  }

  public CapUsersJobConfiguration getConfiguration() {
    return configuration;
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
    configureJobModes();
    bind(new TypeLiteral<BulkWriter<ChangedUserDto>>() {
    }).to(capElasticWriterClass);
    bindConstant().annotatedWith(ElasticsearchBulkSize.class)
        .to(configuration.getElasticSearchBulkSize());
    bindConstant().annotatedWith(PerryApiUrl.class)
        .to(getConfiguration().getPerryApiUrl());
    bindConstant().annotatedWith(PerryApiUser.class)
        .to(getJobsConfiguration().getPerryApiUser());
    bindConstant().annotatedWith(PerryApiPassword.class)
        .to(getJobsConfiguration().getPerryApiPassword());
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
        new LocalDateTimeSavePointContainerService(lastRunLoc);
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);

    return timestampDefaultJobModeService.getCurrentJobMode();
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
}

