package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticUtils;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import java.io.IOException;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class ElasticSearchModule extends AbstractModule {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ElasticSearchModule.class);


  private ElasticsearchConfiguration configuration;

  public ElasticSearchModule(ElasticsearchConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    RestHighLevelClient client = ElasticUtils
        .createAndConfigureESClient(configuration); //must be closed when the job done
    bind(RestHighLevelClient.class).toInstance(client);
    bind(ElasticSearchIndexerDao.class).toInstance(createElasticSearchDao(client, configuration));
  }

  private ElasticSearchIndexerDao createElasticSearchDao(RestHighLevelClient client,
      ElasticsearchConfiguration configuration) {
    ElasticSearchIndexerDao esIndexerDao = new ElasticSearchIndexerDao(client,
        configuration);
    esIndexerDao.createIndexIfMissing();

    return esIndexerDao;
  }

}
