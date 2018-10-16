package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticUtils;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import org.elasticsearch.client.Client;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class ElasticSearchModule extends AbstractModule {

  private ElasticsearchConfiguration configuration;

  ElasticSearchModule(ElasticsearchConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    Client client = ElasticUtils
        .createAndConfigureESClient(configuration); //must be closed when the job done
    bind(Client.class).toInstance(client);
    bind(ElasticSearchIndexerDao.class).toInstance(createElasticSearchDao(client, configuration));
  }

  private ElasticSearchIndexerDao createElasticSearchDao(Client client,
      ElasticsearchConfiguration configuration) {
    ElasticSearchIndexerDao esIndexerDao = new ElasticSearchIndexerDao(client,
        configuration);
    esIndexerDao.createIndexIfMissing();

    return esIndexerDao;
  }

}
