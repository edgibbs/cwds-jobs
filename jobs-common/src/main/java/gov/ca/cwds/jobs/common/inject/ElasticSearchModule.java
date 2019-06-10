package gov.ca.cwds.jobs.common.inject;

import static gov.ca.cwds.jobs.common.util.SavePointUtil.extractProperty;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.elastic.ElasticApiWrapper;
import gov.ca.cwds.jobs.common.elastic.ElasticUtils;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class ElasticSearchModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchModule.class);

  private final Client client;
  private ElasticsearchConfiguration configuration;
  private String indexName;

  public ElasticSearchModule(ElasticsearchConfiguration configuration,
      JobMode jobMode, SavePointContainerService savePointContainerService) {
    this.configuration = configuration;
    this.client = ElasticUtils
        .createAndConfigureESClient(configuration); //must be closed when the job done
    ElasticApiWrapper elasticApiWrapper = new ElasticApiWrapper();
    elasticApiWrapper.setClient(client);
    ElasticsearchService service = new ElasticsearchService();
    service.setClient(client);
    service.setElasticApiWrapper(elasticApiWrapper);
    service.setConfiguration(configuration);
    switch (jobMode) {
      case INITIAL_LOAD:
        indexName = service.createNewIndex();
        break;
      case INCREMENTAL_LOAD:
        indexName = service.getExistingIndex();
        break;
      case INITIAL_RESUME:
        indexName = extractProperty(savePointContainerService.getSavePointFile(), "indexName");
        break;
      default:
        throw new IllegalStateException("Unknown job mode !!!");
    }
    LOGGER.info("Current index name is {}", indexName);
  }

  @Override
  protected void configure() {
    bind(Client.class).toInstance(client);
    bind(ElasticsearchConfiguration.class).toInstance(configuration);
    bindConstant().annotatedWith(IndexName.class).to(indexName);
  }

}
