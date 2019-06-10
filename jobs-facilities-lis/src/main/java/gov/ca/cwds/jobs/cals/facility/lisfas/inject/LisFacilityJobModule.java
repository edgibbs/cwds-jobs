package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.Constants.UnitOfWork;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.inject.FasFacilityServiceProvider;
import gov.ca.cwds.cals.inject.FasFfaSessionFactory;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.inject.LisFacilityServiceProvider;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.service.FasFacilityService;
import gov.ca.cwds.cals.service.LisFacilityService;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisIncrementalFacilityJob;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisInitialFacilityJob;
import gov.ca.cwds.jobs.cals.facility.lisfas.entity.LisChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisIncrementalModeIterator;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisInitialJobModeFinalizer;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisInitialModeIterator;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainerService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainerService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointService;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.IndexAwareSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.math.BigInteger;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class LisFacilityJobModule extends BaseFacilityJobModule<LisFacilityJobConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(LisFacilityJobModule.class);

  public LisFacilityJobModule(LisFacilityJobConfiguration jobConfiguration, JobMode jobMode) {
    super(jobConfiguration, jobMode);
  }

  @Override
  protected void configure() {
    super.configure();
    configureJobModes();
    bind(
        new TypeLiteral<SavePointService<TimestampSavePoint<BigInteger>>>() {
        }).to(LisTimestampSavePointService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<BigInteger>>>() {
        }).annotatedWith(PrimaryContainerService.class).to(LisTimestampSavePointContainerService.class);
    bind(LisChangedEntitiesIdentifiersService.class)
        .toProvider(LisChangedIdentifiersServiceProvider.class);
    bind(LisFacilityService.class).toProvider(LisFacilityServiceProvider.class);
    bind(FasFacilityService.class).toProvider(FasFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDto>>() {
    }).to(LisChangedFacilityService.class);
    bind(
        new TypeLiteral<SavePointService<LicenseNumberSavePoint>>() {
        }).to(LicenseNumberSavePointService.class);
    bind(
        new TypeLiteral<SavePointContainerService<LicenseNumberSavePoint>>() {
        }).annotatedWith(BaseContainerService.class)
        .to(LicenseNumberSavePointContainerService.class);
    bind(
        new TypeLiteral<SavePointContainerService<LicenseNumberSavePoint>>() {
        }).annotatedWith(PrimaryContainerService.class)
        .to(SavePointContainerServiceDecorator.class);
    install(new LisDataAccessModule(getJobConfiguration().getLisDataSourceFactory()));
    install(new FasDataAccessModule(getJobConfiguration().getFasDataSourceFactory()));
  }

  private void configureJobModes() {
    switch (getJobMode()) {
      case INITIAL_RESUME:
      case INITIAL_LOAD:
        bind(Job.class).to(LisInitialFacilityJob.class);
        bind(JobModeFinalizer.class).annotatedWith(SecondaryFinalizer.class)
            .to(LisInitialJobModeFinalizer.class);
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toProvider(
            getPrimaryJobFinalizerProviderClass());
        bind(new TypeLiteral<JobBatchIterator<LicenseNumberSavePoint>>() {
        }).to(LisInitialModeIterator.class);
        break;
      case INCREMENTAL_LOAD:
        bind(Job.class).to(LisIncrementalFacilityJob.class);
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toInstance(() -> {
        });
        bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<BigInteger>>>() {
        }).to(LisIncrementalModeIterator.class);
        break;
      default:
        throw new IllegalStateException(String.format("Unknown job mode %s", getJobMode()));
    }
  }

  @Provides
  public LisFacilityJobConfiguration getJobsConfiguration() {
    return getJobConfiguration();
  }

  @Provides
  @Inject
  public BaseFacilityJobConfiguration getBaseConfiguration() {
    return getJobsConfiguration();
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @FasSessionFactory SessionFactory fasSessionFactory,
      @LisSessionFactory SessionFactory lisSessionFactory,
      @FasFfaSessionFactory SessionFactory fasFfaSessionFactory,
      @CalsnsSessionFactory SessionFactory calsnsDataSourceFactory) {
    try {
      ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap.<String, SessionFactory>builder()
          .put(Constants.UnitOfWork.FAS, fasSessionFactory)
          .put(Constants.UnitOfWork.LIS, lisSessionFactory)
          .put(UnitOfWork.CALSNS, calsnsDataSourceFactory)
          .put(UnitOfWork.FAS_FFA, fasFfaSessionFactory)
          .build();
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory();
      FieldUtils
          .writeField(unitOfWorkAwareProxyFactory, "sessionFactories", sessionFactories, true);
      return unitOfWorkAwareProxyFactory;
    } catch (IllegalAccessException e) {
      LOG.error("Can't build UnitOfWorkAwareProxyFactory", e);
      throw new JobsException(e);
    }
  }

  static class SavePointContainerServiceDecorator extends
      IndexAwareSavePointContainerService<LicenseNumberSavePoint> {

  }

}
