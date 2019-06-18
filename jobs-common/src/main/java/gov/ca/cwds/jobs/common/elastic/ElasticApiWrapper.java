package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 5/10/2019
 */
public class ElasticApiWrapper {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ElasticApiWrapper.class);

  @Inject
  private RestHighLevelClient client;

  void createIndex(CreateIndexRequest request) {
    try {
      client.indices().create(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      LOGGER.error("Unable to create index [" + request.index() + "]", e);
      throw new RuntimeException(e);
    }
  }

  boolean checkAliasExists(GetAliasesRequest request) {
    try {
      return client.indices().existsAlias(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      LOGGER.error("Unable check that alias exists[" + Arrays.toString(request.indices()) + "]", e);
      throw new RuntimeException(e);
    }
  }

  void getAliasesAction(IndicesAliasesRequest request) {
    try {
      client.indices().updateAliases(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      LOGGER.error("Unable to execute aliases request", e);
      throw new RuntimeException(e);
    }
  }

  AcknowledgedResponse deleteIndex(DeleteIndexRequest deleteIndexRequest) {
    try {
      return client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      LOGGER.error("Unable to delete the index [" + Arrays.toString(deleteIndexRequest.indices()) + "]", e);
      throw new RuntimeException(e);
    }
  }

  boolean checkIndicesExists(GetIndexRequest request) {
    try {
      return client.indices().exists(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      LOGGER.error("Unable check that index exists[" + Arrays.toString(request.indices()) + "]", e);
      throw new RuntimeException(e);
    }
  }

  Map<String, Object> getIndexMapping(GetMappingsRequest mappingsRequest, String newIndexName) {
    try {
      MappingMetaData mappingMap =
          client.indices().getMapping(mappingsRequest, RequestOptions.DEFAULT).mappings().get(newIndexName);
      return mappingMap.getSourceAsMap();
    } catch (IOException e) {
      LOGGER.error("Unable get mapping for index [" + newIndexName + "]", e);
      throw new RuntimeException(e);
    }
  }

  public void setClient(RestHighLevelClient client) {
    this.client = client;
  }

}
