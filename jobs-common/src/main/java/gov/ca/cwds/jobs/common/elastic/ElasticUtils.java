package gov.ca.cwds.jobs.common.elastic;

import gov.ca.cwds.rest.api.ApiException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ievgenii Drozd
 * @version 2/27/18
 */
public final class ElasticUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticUtils.class);

  private ElasticUtils() {}

  public static RestHighLevelClient createAndConfigureESClient(ElasticsearchConfiguration config) {
    try {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(config.getUser(), config.getPassword()));

      RestClientBuilder restClientBuilder = RestClient.builder(getHttpHosts(config.getNodes())).setHttpClientConfigCallback(
          httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

      RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
      return client;
    } catch (RuntimeException e) {
      LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
      throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
    }
  }

  public static HttpHost[] getHttpHosts(List<String> nodes) {
    List<HttpHost> nodesList = new ArrayList<>();
    for (String node : nodes) {
      String[] hostPortPair = node.split(":");
      String host = getHost(hostPortPair);
      int port = getPort(hostPortPair);
      if (org.jadira.usertype.spi.utils.lang.StringUtils.isNotEmpty(host)) {
        nodesList.add(new HttpHost(host, port));
      } else {
        LOGGER.warn("There is an empty host for port {}", port);
      }
    }
    return nodesList.toArray(new HttpHost[0]);
  }

  @SuppressWarnings("fb-contrib:CLI_CONSTANT_LIST_INDEX")
  private static int getPort(String[] hostPortPair) {
    return hostPortPair.length > 1 && hostPortPair[1] != null ? Integer.parseInt(hostPortPair[1])
        : -1;
  }

  private static String getHost(String[] hostPortPair) {
    return hostPortPair.length > 0 ? hostPortPair[0] : "";
  }
}
