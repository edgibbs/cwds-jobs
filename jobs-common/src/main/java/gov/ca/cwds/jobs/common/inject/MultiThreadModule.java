package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.configuration.MultiThreadConfiguration;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public class MultiThreadModule extends AbstractModule {

  private MultiThreadConfiguration configuration;

  public MultiThreadModule(MultiThreadConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(JobBatchSize.class)
        .to(configuration.getBatchSize());
    bindConstant().annotatedWith(ElasticsearchBulkSize.class)
        .to(configuration.getElasticSearchBulkSize());
    bindConstant().annotatedWith(ReaderThreadsCount.class)
        .to(configuration.getReaderThreadsCount());
  }

}
