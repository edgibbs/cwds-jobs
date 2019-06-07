package gov.ca.cwds.jobs.audit;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.audit.inject.AuditEventServiceProvider;
import gov.ca.cwds.jobs.audit.inject.AuditInitialJobModeFinalizerProvider;
import gov.ca.cwds.jobs.audit.inject.IncrementalModeAuditEventIdentifiersServiceProvider;
import gov.ca.cwds.jobs.audit.inject.InitialModeAuditEventIdentifiersServiceProvider;
import gov.ca.cwds.jobs.audit.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchAliasFinalizerProvider;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.iterator.LocalDateTimeJobBatchIterator;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeService;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeService;
import gov.ca.cwds.jobs.common.savepoint.IndexAwareSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;

public class AuditEventsJobModule extends AbstractModule {

  private Class<? extends Provider<? extends JobModeFinalizer>> primaryJobFinalizerProviderClass;
  private AuditEventsJobConfiguration configuration;

  private Class<? extends BulkWriter<AuditEventChangedDto>> auditEventWriterClass;
  private JobMode jobMode;

  public AuditEventsJobModule(AuditEventsJobConfiguration jobConfiguration,
      JobMode jobMode) {
    this.jobMode = jobMode;
    this.auditEventWriterClass = AuditEventElasticWriter.class;
    this.primaryJobFinalizerProviderClass = ElasticsearchAliasFinalizerProvider.class;
    this.configuration = jobConfiguration;
  }

  public void setAuditEventWriterClass(
      Class<? extends BulkWriter<AuditEventChangedDto>> auditEventWriterClass) {
    this.auditEventWriterClass = auditEventWriterClass;
  }

  public void setPrimaryJobFinalizerClass(
      Class<? extends Provider<? extends JobModeFinalizer>> primaryJobFinalizerProviderClass) {
    this.primaryJobFinalizerProviderClass = primaryJobFinalizerProviderClass;
  }

  @Provides
  public AuditEventsJobConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<BulkWriter<AuditEventChangedDto>>() {
    }).to(auditEventWriterClass);
    bind(Job.class).to(AuditEventsJob.class);
    bind(new TypeLiteral<JobModeService>() {
    }).to(LocalDateTimeJobModeService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {
        }).annotatedWith(BaseContainerService.class)
        .to(LocalDateTimeSavePointContainerService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {
        }).annotatedWith(PrimaryContainerService.class)
        .to(SavePointContainerServiceDecorator.class);
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeSavePointService.class);
    bind(new TypeLiteral<ChangedEntityService<AuditEventChangedDto>>() {
    }).toProvider(AuditEventServiceProvider.class);
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeJobBatchIterator.class);
    install(new NsDataAccessModule(configuration.getNsDataSourceFactory()));
    bindJobModeImplementor();
  }

  private void bindJobModeImplementor() {
    switch (jobMode) {
      case INITIAL_RESUME:
      case INITIAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(SecondaryFinalizer.class)
            .toProvider(AuditInitialJobModeFinalizerProvider.class);
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class)
            .toProvider(primaryJobFinalizerProviderClass);
        bind(
            new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {
            }).toProvider(InitialModeAuditEventIdentifiersServiceProvider.class);
        break;
      case INCREMENTAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toInstance(() -> {
        });
        bind(
            new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {
            }).toProvider(IncrementalModeAuditEventIdentifiersServiceProvider.class);
        break;
      default:
        throw new IllegalStateException(String.format("Unknown job mode %s", jobMode));
    }
  }

  static class SavePointContainerServiceDecorator extends
      IndexAwareSavePointContainerService<TimestampSavePoint<LocalDateTime>> {

  }


}

