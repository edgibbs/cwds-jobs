package gov.ca.cwds.jobs.cals.facility.cws.inject;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.Constants.UnitOfWork;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.inject.CwsFacilityServiceProvider;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.cals.service.LegacyDictionariesCache;
import gov.ca.cwds.cals.service.LegacyDictionariesCache.LegacyDictionariesCacheBuilder;
import gov.ca.cwds.cms.data.access.mapper.CountyOwnershipMapper;
import gov.ca.cwds.cms.data.access.mapper.ExternalInterfaceMapper;
import gov.ca.cwds.data.legacy.cms.dao.CountiesDao;
import gov.ca.cwds.data.legacy.cms.dao.LicenseStatusDao;
import gov.ca.cwds.data.legacy.cms.dao.StateDao;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.County;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.LicenseStatus;
import gov.ca.cwds.data.legacy.cms.entity.syscodes.State;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.cws.CwsFacilityJob;
import gov.ca.cwds.jobs.cals.facility.cws.CwsFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.cws.entity.CwsChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.cws.mode.CwsJobIncrementalModeImplementor;
import gov.ca.cwds.jobs.cals.facility.cws.mode.CwsJobInitialModeImplementor;
import gov.ca.cwds.jobs.cals.facility.cws.mode.CwsJobInitialResumeModeImplementor;
import gov.ca.cwds.jobs.cals.facility.cws.savepoint.CwsTimestampSavePointService;
import gov.ca.cwds.jobs.common.api.JobModeImplementor;
import gov.ca.cwds.jobs.common.api.JobModeService;
import gov.ca.cwds.jobs.common.config.JobOptions;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeDefaultJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.time.LocalDateTime;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class CwsFacilityJobModule extends BaseFacilityJobModule {

  public CwsFacilityJobModule(String[] args) {
    super(args);
  }

  private static final Logger LOG = LoggerFactory.getLogger(CwsFacilityJobModule.class);

  @Override
  protected void configure() {
    super.configure();
    bind(Job.class).to(CwsFacilityJob.class);
    bind(new TypeLiteral<JobModeService<DefaultJobMode>>() {
    }).to(LocalDateTimeDefaultJobModeService.class);
    bindJobModeImplementor();
    bind(
        new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
        }).to(LocalDateTimeSavePointContainerService.class);
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
    }).toProvider(CwsTimestampSavePointServiceProvider.class);
    bind(CwsTimestampSavePointService.class).toProvider(CwsTimestampSavePointServiceProvider.class);
    bind(
        new TypeLiteral<ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>>>() {
        }).toProvider(CwsChangedIdentifiersServiceProvider.class);
    bind(CwsFacilityService.class).toProvider(CwsFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDto>>() {
    }).to(CwsChangedFacilityService.class);
    bind(CountyOwnershipMapper.class).to(CountyOwnershipMapper.INSTANCE.getClass())
        .asEagerSingleton();
    bind(ExternalInterfaceMapper.class).to(ExternalInterfaceMapper.INSTANCE.getClass())
        .asEagerSingleton();

    install(new CwsCmsRsDataAccessModule());
  }

  private void bindJobModeImplementor() {
    Class<? extends JobModeImplementor<ChangedFacilityDto, TimestampSavePoint<LocalDateTime>, DefaultJobMode>> clazz = null;

    LocalDateTimeDefaultJobModeService timestampDefaultJobModeService =
        new LocalDateTimeDefaultJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(getJobOptions().getLastRunLoc());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    switch (timestampDefaultJobModeService.getCurrentJobMode()) {
      case INITIAL_LOAD:
        clazz = CwsJobInitialModeImplementor.class;
        break;
      case INITIAL_LOAD_RESUME:
        clazz = CwsJobInitialResumeModeImplementor.class;
        break;
      case INCREMENTAL_LOAD:
        clazz = CwsJobIncrementalModeImplementor.class;
        break;
    }
    bind(
        new TypeLiteral<JobModeImplementor<ChangedFacilityDto, TimestampSavePoint<LocalDateTime>, DefaultJobMode>>() {
        }).to(clazz);
  }

  @Provides
  @Override
  @Inject
  public CwsFacilityJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
    return super.getJobsConfiguration(jobOptions, CwsFacilityJobConfiguration.class);
  }

  @Provides
  @Inject
  public BaseFacilityJobConfiguration getBaseConfiguration(JobOptions jobOptions) {
    return getJobsConfiguration(jobOptions);
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @CmsSessionFactory SessionFactory cwsSessionFactory,
      @CalsnsSessionFactory SessionFactory calsnsDataSourceFactory) {
    try {
      ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap.<String, SessionFactory>builder()
          .put(Constants.UnitOfWork.CMS, cwsSessionFactory)
          .put(UnitOfWork.CALSNS, calsnsDataSourceFactory)
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

  @Provides
  public LegacyDictionariesCache provideLegacyDictionariesCache(
      CountiesDao countiesDao,
      StateDao stateDao,
      LicenseStatusDao licenseStatusDao
  ) {
    LegacyDictionariesCacheBuilder builder = new LegacyDictionariesCacheBuilder();
    return builder
        .add(County.class, countiesDao)
        .add(State.class, stateDao)
        .add(LicenseStatus.class, licenseStatusDao)
        .build();
  }
}
