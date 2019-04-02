package gov.ca.cwds.jobs.common;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.entity.TestEntity;
import gov.ca.cwds.jobs.common.entity.TestEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.inject.TestEntityServiceProvider;
import gov.ca.cwds.jobs.common.inject.TestIdentifiersServiceProvider;
import gov.ca.cwds.jobs.common.inject.TestSessionFactory;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.iterator.LocalDateTimeJobBatchIterator;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.time.LocalDateTime;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public class TestCustomModule extends AbstractModule {

  private TestJobConfiguration configuration;
  private String runDir;
  private Class<? extends Provider<? extends TestEntityService>> changedEntityServiceProvider;

  public TestCustomModule(TestJobConfiguration configuration, String runDir) {
    this.configuration = configuration;
    this.runDir = runDir;
    this.changedEntityServiceProvider = TestEntityServiceProvider.class;
  }

  public void setChangedEntityServiceProvider(
      Class<? extends Provider<? extends TestEntityService>> changedEntityService) {
    this.changedEntityServiceProvider = changedEntityService;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<TestJobConfiguration>() {
    }).toInstance(configuration);
    bind(Job.class).to(TestJobImpl.class);
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeJobBatchIterator.class);
    if (getCurrentJobMode(runDir) == JobMode.INITIAL_LOAD) {
      bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class)
          .to(LocalDateTimeJobModeFinalizer.class);
    } else {
      bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toInstance(() -> {
      });
    }
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeSavePointService.class);
    bind(new TypeLiteral<ChangedEntityService<TestEntity>>() {
    }).toProvider(changedEntityServiceProvider);
    bind(new TypeLiteral<BulkWriter<TestEntity>>() {
    }).to(TestEntityWriter.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {
        }).to(LocalDateTimeSavePointContainerService.class);
    bind(new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {
    }).toProvider(TestIdentifiersServiceProvider.class);
  }

  private JobMode getCurrentJobMode(String runDir) {
    LocalDateTimeJobModeService timestampDefaultJobModeService =
        new LocalDateTimeJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(runDir);
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    return timestampDefaultJobModeService.getCurrentJobMode();
  }

  static class TestEntityWriter extends TestWriter<TestEntity> {

  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @TestSessionFactory SessionFactory testSessionFactory) {
    return new UnitOfWorkAwareProxyFactory("test", testSessionFactory);
  }

}
