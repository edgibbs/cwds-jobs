package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD_RESUME;

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
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisConnx11IncrementaJobModeImplementor;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisInitialModeImplementor;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisInitialResumeModeImplementor;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainerService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainerService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointService;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
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
public class LisFacilityJobModule extends BaseFacilityJobModule {

  private static final Logger LOG = LoggerFactory.getLogger(LisFacilityJobModule.class);

  public LisFacilityJobModule(String[] args) {
    super(args, lastRunDir);
  }

  @Override
  protected void configure() {
    super.configure();
    configureJobModes();
    bind(
        new TypeLiteral<SavePointService<TimestampSavePoint<BigInteger>, DefaultJobMode>>() {
        }).to(LisTimestampSavePointService.class);
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<BigInteger>, DefaultJobMode>>() {
        }).to(LisTimestampSavePointContainerService.class);
    bind(LisChangedEntitiesIdentifiersService.class)
        .toProvider(LisChangedIdentifiersServiceProvider.class);
    bind(LisFacilityService.class).toProvider(LisFacilityServiceProvider.class);
    bind(FasFacilityService.class).toProvider(FasFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDto>>() {
    }).to(LisChangedFacilityService.class);
    bind(
        new TypeLiteral<SavePointService<LicenseNumberSavePoint, DefaultJobMode>>() {
        }).to(LicenseNumberSavePointService.class);
    bind(
        new TypeLiteral<SavePointContainerService<LicenseNumberSavePoint, DefaultJobMode>>() {
        }).to(LicenseNumberSavePointContainerService.class);
    install(new LisDataAccessModule());
    install(new FasDataAccessModule());
  }

  private void configureJobModes() {
    DefaultJobMode jobMode = defineJobMode();
    if (jobMode == INITIAL_LOAD || jobMode == INITIAL_LOAD_RESUME) {
      configureInitialMode(jobMode);
    } else {
      configureIncrementalMode();
    }
  }

  private void configureIncrementalMode() {
    bind(Job.class).to(LisIncrementalFacilityJob.class);
    bind(
        new TypeLiteral<JobModeImplementor<ChangedFacilityDto, TimestampSavePoint<BigInteger>, DefaultJobMode>>() {
        }).to(LisConnx11IncrementaJobModeImplementor.class);
  }

  private void configureInitialMode(DefaultJobMode jobMode) {
    bind(Job.class).to(LisInitialFacilityJob.class);
    Class<? extends JobModeImplementor<ChangedFacilityDto, LicenseNumberSavePoint, DefaultJobMode>> jobModeImplementorClass = null;
    if (jobMode == INITIAL_LOAD) {
      jobModeImplementorClass = LisInitialModeImplementor.class;
    } else {
      jobModeImplementorClass = LisInitialResumeModeImplementor.class;
    }
    bind(
        new TypeLiteral<JobModeImplementor<ChangedFacilityDto, LicenseNumberSavePoint, DefaultJobMode>>() {
        }).to(jobModeImplementorClass);
  }

  private DefaultJobMode defineJobMode() {
    LisJobModeService timestampDefaultJobModeService =
        new LisJobModeService();
    LicenseNumberSavePointContainerService savePointContainerService =
        new LicenseNumberSavePointContainerService(getJobOptions().getLastRunLoc());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    return timestampDefaultJobModeService.getCurrentJobMode();
  }

  @Provides
  @Override
  @Inject
  public LisFacilityJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
    return super.getJobsConfiguration(jobOptions, LisFacilityJobConfiguration.class);
  }

  @Provides
  @Inject
  public BaseFacilityJobConfiguration getBaseConfiguration(JobOptions jobOptions) {
    return getJobsConfiguration(jobOptions);
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

}
