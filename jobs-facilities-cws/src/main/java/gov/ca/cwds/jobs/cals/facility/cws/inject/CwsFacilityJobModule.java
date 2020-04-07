package gov.ca.cwds.jobs.cals.facility.cws.inject;

import java.time.LocalDateTime;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.ca.cwds.cms.data.access.mapper.CountyOwnershipMapperImpl;
import gov.ca.cwds.cms.data.access.mapper.ExternalInterfaceMapper;
import gov.ca.cwds.cms.data.access.mapper.ExternalInterfaceMapperImpl;
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
import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants;
import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants.IncrementalMode;
import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants.InitialMode;
import gov.ca.cwds.jobs.cals.facility.cws.entity.CwsChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.cws.savepoint.CwsTimestampSavePointService;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.iterator.LocalDateTimeJobBatchIterator;
import gov.ca.cwds.jobs.common.mode.JobBatchMode;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeService;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeService;
import gov.ca.cwds.jobs.common.savepoint.IndexAwareSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class CwsFacilityJobModule extends BaseFacilityJobModule<CwsFacilityJobConfiguration> {

  private static final Logger LOG = LoggerFactory.getLogger(CwsFacilityJobModule.class);

  public CwsFacilityJobModule(CwsFacilityJobConfiguration jobConfiguration, JobMode jobMode) {
    super(jobConfiguration, jobMode);
  }

  @Override
  protected void configure() {
    super.configure();
    bind(Job.class).to(CwsFacilityJob.class);
    bind(new TypeLiteral<JobModeService>() {}).to(LocalDateTimeJobModeService.class);
    bindJobModeImplementor();
    bind(new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {})
        .annotatedWith(BaseContainerService.class).to(LocalDateTimeSavePointContainerService.class);
    bind(new TypeLiteral<SavePointContainerService<TimestampSavePoint<LocalDateTime>>>() {})
        .annotatedWith(PrimaryContainerService.class).to(SavePointContainerServiceDecorator.class);
    bind(new TypeLiteral<SavePointService<TimestampSavePoint<LocalDateTime>>>() {})
        .toProvider(CwsTimestampSavePointServiceProvider.class);
    bind(CwsTimestampSavePointService.class).toProvider(CwsTimestampSavePointServiceProvider.class);
    bind(new TypeLiteral<ChangedEntitiesIdentifiersService<LocalDateTime>>() {})
        .toProvider(CwsChangedIdentifiersServiceProvider.class);
    bind(CwsFacilityService.class).toProvider(CwsFacilityServiceProvider.class);
    bind(new TypeLiteral<ChangedEntityService<ChangedFacilityDto>>() {})
        .to(CwsChangedFacilityService.class);
    bind(CountyOwnershipMapper.class).to(CountyOwnershipMapperImpl.class);
    bind(ExternalInterfaceMapper.class).to(ExternalInterfaceMapperImpl.class);
    bind(new TypeLiteral<JobBatchIterator<TimestampSavePoint<LocalDateTime>>>() {})
        .to(LocalDateTimeJobBatchIterator.class);

    install(new CwsCmsRsDataAccessModule(getJobConfiguration().getCmsDataSourceFactory()));
  }

  private void bindJobModeImplementor() {
    bindConstant().annotatedWith(JobBatchMode.class).to(getJobMode());
    bindConstant().annotatedWith(CwsGetFirstTimestampAfterSavePointQuery.class)
        .to(IncrementalMode.GET_FIRST_TS_AFTER_SAVEPOINT_QUERY);

    switch (getJobMode()) {
      case INITIAL_RESUME:
      case INITIAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(SecondaryFinalizer.class)
            .toProvider(CwsInitialModeFinalizerProvider.class);
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class)
            .toProvider(getPrimaryJobFinalizerProviderClass());
        bindConstant().annotatedWith(CwsGetIdentifiersAfterTimestampQuery.class)
            .to(QueryConstants.InitialMode.GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY);
        bindConstant().annotatedWith(CwsGetIdentifiersBetweenTimestampsQuery.class)
            .to(QueryConstants.InitialMode.GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY);
        bindConstant().annotatedWith(CwsGetNextSavePointQuery.class)
            .to(InitialMode.GET_NEXT_SAVEPOINT_QUERY);
        break;
      case INCREMENTAL_LOAD:
        bind(JobModeFinalizer.class).annotatedWith(PrimaryFinalizer.class).toInstance(() -> {
        });
        bindConstant().annotatedWith(CwsGetIdentifiersAfterTimestampQuery.class)
            .to(IncrementalMode.GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY);
        bindConstant().annotatedWith(CwsGetIdentifiersBetweenTimestampsQuery.class)
            .to(IncrementalMode.GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY);
        bindConstant().annotatedWith(CwsGetNextSavePointQuery.class)
            .to(IncrementalMode.GET_NEXT_SAVEPOINT_QUERY);
        break;
      default:
        throw new IllegalStateException(String.format("Unknown job mode %s", getJobMode()));
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
      ImmutableMap<String, SessionFactory> sessionFactories = ImmutableMap
          .<String, SessionFactory>builder().put(Constants.UnitOfWork.CMS, cwsSessionFactory)
          .put(UnitOfWork.CALSNS, calsnsDataSourceFactory).build();
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory = new UnitOfWorkAwareProxyFactory();
      FieldUtils.writeField(unitOfWorkAwareProxyFactory, "sessionFactories", sessionFactories,
          true);
      return unitOfWorkAwareProxyFactory;
    } catch (IllegalAccessException e) {
      LOG.error("Can't build UnitOfWorkAwareProxyFactory", e);
      throw new JobsException(e);
    }
  }

  @Provides
  public LegacyDictionariesCache provideLegacyDictionariesCache(CountiesDao countiesDao,
      StateDao stateDao, LicenseStatusDao licenseStatusDao) {
    return new LegacyDictionariesCacheBuilder().add(County.class, countiesDao)
        .add(State.class, stateDao).add(LicenseStatus.class, licenseStatusDao).build();
  }

  static class SavePointContainerServiceDecorator
      extends IndexAwareSavePointContainerService<TimestampSavePoint<LocalDateTime>> {

  }

}
