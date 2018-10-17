package gov.ca.cwds.jobs.cals.facility.cws.inject;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;

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
import gov.ca.cwds.jobs.cals.facility.cws.savepoint.CwsTimestampSavePointService;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.iterator.LocalDateTimeJobBatchIterator;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeService;
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
public class CwsFacilityJobModule extends BaseFacilityJobModule<CwsFacilityJobConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(CwsFacilityJobModule.class);

  public CwsFacilityJobModule(CwsFacilityJobConfiguration jobConfiguration, String lastRunDir) {
    super(jobConfiguration, lastRunDir);
  }

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
        new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {
        }).toProvider(CwsChangedIdentifiersServiceProvider.class);
    bind(CwsFacilityService.class).toProvider(CwsFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDto>>() {
    }).to(CwsChangedFacilityService.class);
    bind(CountyOwnershipMapper.class).to(CountyOwnershipMapper.INSTANCE.getClass())
        .asEagerSingleton();
    bind(ExternalInterfaceMapper.class).to(ExternalInterfaceMapper.INSTANCE.getClass())
        .asEagerSingleton();
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {
    }).to(LocalDateTimeJobBatchIterator.class);

    install(new CwsCmsRsDataAccessModule());
  }

  private void bindJobModeImplementor() {
    LocalDateTimeDefaultJobModeService timestampDefaultJobModeService =
        new LocalDateTimeDefaultJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(getLastRunDir());
    timestampDefaultJobModeService.setSavePointContainerService(savePointContainerService);
    if (timestampDefaultJobModeService.getCurrentJobMode() == INITIAL_LOAD) {
      bind(JobModeFinalizer.class).toProvider(CwsInitialModeFinalizerProvider.class);
      bindConstant().annotatedWith(TimestampField.class).to("lastUpdatedTime");
      bindConstant().annotatedWith(CwsIdentifierCreator.class)
          .to("new CwsChangedIdentifier(home.identifier, home.lastUpdatedTime)");
    } else { //incremental load
      bind(JobModeFinalizer.class).toInstance(() -> {
      });
      bindConstant().annotatedWith(TimestampField.class).to("replicationLastUpdated");
      bindConstant().annotatedWith(CwsIdentifierCreator.class)
          .to("new CwsChangedIdentifier(home.identifier, "
              + "home.recordChangeOperation, home.replicationLastUpdated)");
    }
  }

  @Provides
  public CwsFacilityJobConfiguration getJobsConfiguration() {
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
