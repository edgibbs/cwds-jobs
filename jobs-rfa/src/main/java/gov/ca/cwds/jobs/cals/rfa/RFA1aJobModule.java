package gov.ca.cwds.jobs.cals.rfa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import gov.ca.cwds.cals.Constants;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.cals.service.rfa.RFA1aFormsCollectionService;
import gov.ca.cwds.inject.NsSessionFactory;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import gov.ca.cwds.jobs.common.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.api.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.api.ChangedEntityService;
import gov.ca.cwds.jobs.common.batch.JobBatchIterator;
import gov.ca.cwds.jobs.common.batch.JobBatchIteratorImpl;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import gov.ca.cwds.jobs.common.inject.AbstractBaseJobModule;
import gov.ca.cwds.jobs.common.job.BulkWriter;
import gov.ca.cwds.jobs.common.job.Job;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class RFA1aJobModule extends AbstractBaseJobModule {

  public RFA1aJobModule(String[] args) {
    super(args);
  }

  @Override
  protected void configure() {
    super.configure();
    install(new MappingModule());
    install(new NsDataAccessModule());
    //TODO bind job here bind(Job.class).to(SomeJob.class);
    bind(BulkWriter.class).to(RFA1aFormElasticWriter.class);
    bind(ChangedEntitiesIdentifiersService.class).toProvider(ChangedRFAIdentifiersProvider.class);
    bind(ChangedEntityService.class).toProvider(ChangedRFAServiceProvider.class);
    bind(JobBatchIterator.class).to(JobBatchIteratorImpl.class);
    bind(RFA1aFormsCollectionService.class);
    bind(ChangedRFA1aFormsService.class);
    bind(Job.class).to(RFA1aJob.class).in(Singleton.class);
  }

  @Provides
  @Override
  @Inject
  public RFA1aJobConfiguration getJobsConfiguration(JobOptions jobOptions) {
    RFA1aJobConfiguration jobConfiguration = BaseJobConfiguration
        .getJobsConfiguration(RFA1aJobConfiguration.class,
            jobOptions.getEsConfigLoc());
    jobConfiguration.setDocumentMapping("rfa.mapping.json");
    jobConfiguration.setIndexSettings("rfa.settings.json");
    return jobConfiguration;
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @NsSessionFactory SessionFactory nsSessionFactory) {
    return new UnitOfWorkAwareProxyFactory(Constants.UnitOfWork.CALSNS, nsSessionFactory);
  }

  static class RFA1aFormElasticWriter extends ElasticWriter<ChangedRFA1aFormDTO> {

    /**
     * Constructor.
     *
     * @param elasticsearchDao ES DAO
     * @param objectMapper Jackson object mapper
     */
    @Inject
    public RFA1aFormElasticWriter(ElasticSearchIndexerDao elasticsearchDao,
        ObjectMapper objectMapper) {
      super(elasticsearchDao, objectMapper);
    }
  }

}
