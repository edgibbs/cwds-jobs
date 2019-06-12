package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Map;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;

/**
 * Created by Alexander Serbin on 5/10/2019
 */
public class ElasticApiWrapper {

  @Inject
  private RestHighLevelClient client;

  void createIndex(CreateIndexRequest request) throws IOException {
    client.indices().create(request, RequestOptions.DEFAULT);
  }

  boolean checkAliasExists(GetAliasesRequest request) throws IOException {
    return client.indices().existsAlias(request, RequestOptions.DEFAULT);
  }

  void getAliasesAction(IndicesAliasesRequest request) {
    client.indices().aliases(request).actionGet();
  }

  AcknowledgedResponse deleteIndex(DeleteIndexRequest deleteIndexRequest) throws IOException {
    return client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
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

  public void setClient(RestHighLevelClient client) {
    this.client = client;
  }

}
