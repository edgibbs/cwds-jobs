package gov.ca.cwds.jobs.common.inject;

import static gov.ca.cwds.jobs.common.mode.JobMode.INITIAL_LOAD;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.elastic.ElasticApiWrapper;
import gov.ca.cwds.jobs.common.elastic.ElasticUtils;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import org.elasticsearch.client.Client;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class ElasticSearchModule extends AbstractModule {


  private final Client client;
  private ElasticsearchConfiguration configuration;
  private String indexName;

  public ElasticSearchModule(ElasticsearchConfiguration configuration,
      JobMode jobMode) {
    this.configuration = configuration;
    this.client = ElasticUtils
        .createAndConfigureESClient(configuration); //must be closed when the job done
    ElasticApiWrapper elasticApiWrapper = new ElasticApiWrapper();
    elasticApiWrapper.setClient(client);
    ElasticsearchService service = new ElasticsearchService();
    service.setClient(client);
    service.setElasticApiWrapper(elasticApiWrapper);
    service.setConfiguration(configuration);
    indexName = jobMode == INITIAL_LOAD ? service.createNewIndex() : service.getExistingIndex();
  }

  @Override
  protected void configure() {
    bind(Client.class).toInstance(client);
    bind(ElasticsearchConfiguration.class).toInstance(configuration);
    bindConstant().annotatedWith(IndexName.class).to(indexName);
  }

}
