package gov.ca.cwds.jobs.audit;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.audit.inject.AuditEventIdentifiersServiceProvider;
import gov.ca.cwds.jobs.audit.inject.AuditEventServiceProvider;
import gov.ca.cwds.jobs.audit.inject.AuditInitialJobModeFinalizerProvider;
import gov.ca.cwds.jobs.audit.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.iterator.LocalDateTimeJobBatchIterator;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeService;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeDefaultJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;

public class AuditEventsJobModule extends AbstractModule {

  private AuditEventsJobConfiguration configuration;
  private final String lastRunDir;

  private Class<? extends BulkWriter<AuditEventChangedDto>> auditEventWriterClass;

  public AuditEventsJobModule(AuditEventsJobConfiguration jobConfiguration, String lastRunDir) {
    this.auditEventWriterClass = AuditEventElasticWriter.class;
    this.configuration = jobConfiguration;
    this.lastRunDir = lastRunDir;
  }

  public String getLastRunDir() {
    return lastRunDir;
  }

  public void setAuditEventWriterClass(
      Class<? extends BulkWriter<AuditEventChangedDto>> auditEventWriterClass) {
    this.auditEventWriterClass = auditEventWriterClass;
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
    bind(new TypeLiteral<JobModeService<DefaultJobMode>>() {
    }).to(LocalDateTimeDefaultJobModeService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
        }).to(LocalDateTimeSavePointContainerService.class);
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
    }).to(LocalDateTimeSavePointService.class);
    bind(
        new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {
        }).toProvider(AuditEventIdentifiersServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<AuditEventChangedDto>>() {
    }).toProvider(AuditEventServiceProvider.class);
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeJobBatchIterator.class);
    install(new NsDataAccessModule());
    bindJobModeImplementor();
  }

  private void bindJobModeImplementor() {
    LocalDateTimeDefaultJobModeService timestampDefaultJobModeService =
        new LocalDateTimeDefaultJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(getLastRunDir());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    if (timestampDefaultJobModeService.getCurrentJobMode() == INITIAL_LOAD) {
      bind(JobModeFinalizer.class).toProvider(AuditInitialJobModeFinalizerProvider.class);
    } else { //incremental load
      bind(JobModeFinalizer.class).toInstance(() -> {
      });
    }
  }

}

