package gov.ca.cwds.jobs.common.elastic;

import gov.ca.cwds.rest.api.ApiException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ievgenii Drozd
 * @version 2/27/18
 */
public final class ElasticUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticUtils.class);

  private ElasticUtils() {}

  public static TransportClient createAndConfigureESClient(ElasticsearchConfiguration config) {
    TransportClient client = null;

    LOGGER.info("Create NEW ES client");
    try {
      Settings.Builder settings =
          Settings.builder().put("cluster.name", config.getElasticsearchCluster());
      client = XPackUtils.secureClient(config.getUser(), config.getPassword(), settings);

      for (TransportAddress address : getValidatedESNodes(config)) {
        client.addTransportAddress(address);
      }
    } catch (RuntimeException e) {
      LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
      if (client != null) {
        client.close();
      }
      throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
    }
    return client;
  }

  private static List<TransportAddress> getValidatedESNodes(
      ElasticsearchConfiguration config) {
    List<TransportAddress> nodesList = new LinkedList<>();
    String[] params;
    List<String> nodes = config.getNodes();
    Map<String, String> hostPortMap = new HashMap<>(nodes.size());

    hostPortMap.put(config.getElasticsearchHost(), config.getElasticsearchPort());
    for (String node : nodes) {
      params = node.split(":");
      hostPortMap.put(params[0], params[1]);
    }

    hostPortMap.forEach((k, v) -> {
      if ((null != k) && (null != v)) {
        LOGGER.info("Adding new ES Node host:[{}] port:[{}] to elasticsearch client", k, v);
        try {
          nodesList
              .add(new TransportAddress(InetAddress.getByName(k), Integer.parseInt(v)));
        } catch (UnknownHostException e) {
          LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
          throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
        }
      }
    });

    return nodesList;
  }
}
