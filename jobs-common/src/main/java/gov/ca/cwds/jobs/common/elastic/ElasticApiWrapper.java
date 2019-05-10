package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;

/**
 * Created by Alexander Serbin on 5/10/2019
 */
public class ElasticApiWrapper {

  @Inject
  private Client client;

  void createIndex(CreateIndexRequestBuilder builder) {
    CreateIndexRequest indexRequest = builder.request();
    client.admin().indices().create(indexRequest).actionGet();
  }

  CreateIndexRequestBuilder prepareCreateIndexBuilder(String indexName) {
    return client.admin().indices().prepareCreate(indexName);
  }

  boolean checkAliasExists(GetAliasesRequest request) {
    return client.admin().indices().aliasesExist(request).actionGet().exists();
  }

  void getAliasesAction(IndicesAliasesRequest request) {
    client.admin().indices().aliases(request).actionGet();
  }

  DeleteIndexResponse deleteIndex(DeleteIndexRequest deleteIndexRequest) {
    return client.admin().indices().delete(deleteIndexRequest).actionGet();
  }

  boolean checkIndicesExists(IndicesExistsRequest request) {
    return client.admin().indices().exists(request).actionGet().isExists();
  }

  Map<String, Object> getIndexMapping(GetMappingsRequest mappingsRequest, String newIndexName)
      throws IOException {
    ImmutableOpenMap<String, MappingMetaData> mappingMap =
        client.admin().indices().getMappings(mappingsRequest).actionGet().mappings()
            .get(newIndexName);
    return mappingMap.values().iterator().next().value.getSourceAsMap();
  }

  public void setClient(Client client) {
    this.client = client;
  }

}
