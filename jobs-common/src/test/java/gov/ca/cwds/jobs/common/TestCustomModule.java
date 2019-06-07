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
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.IndexName;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
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
import gov.ca.cwds.jobs.common.savepoint.IndexAwareSavePointContainerService;
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

  public static final String INDEX_NAME = "INDEX_NAME";

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
    bindConstant().annotatedWith(IndexName.class).to(INDEX_NAME);
    bind(new TypeLiteral<TestJobConfiguration>() {
    }).toInstance(configuration);
    bind(Job.class).to(TestJobImpl.class);
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeJobBatchIterator.class);
    switch (getCurrentJobMode(runDir)) {
      case INITIAL_RESUME: case INITIAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class)
            .to(LocalDateTimeJobModeFinalizer.class);
        break;
      case INCREMENTAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toInstance(() -> {
        });
        break;
      default: throw new IllegalStateException("Unknown job mode");
    }
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeSavePointService.class);
    bind(new TypeLiteral<ChangedEntityService<TestEntity>>() {
    }).toProvider(changedEntityServiceProvider);
    bind(new TypeLiteral<BulkWriter<TestEntity>>() {
    }).to(TestEntityWriter.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {
        }).annotatedWith(BaseContainerService.class)
        .to(LocalDateTimeSavePointContainerService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {
        }).annotatedWith(PrimaryContainerService.class)
        .to(SavePointContainerServiceDecorator.class);
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

  static class SavePointContainerServiceDecorator extends
      IndexAwareSavePointContainerService<TimestampSavePoint<LocalDateTime>> {

  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @TestSessionFactory SessionFactory testSessionFactory) {
    return new UnitOfWorkAwareProxyFactory("test", testSessionFactory);
  }

}
