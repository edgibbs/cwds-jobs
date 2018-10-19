package gov.ca.cwds.jobs.common.elastic;

import gov.ca.cwds.rest.api.ApiException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
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
    try {
      client = makeESTransportClient(config);
      for (TransportAddress address : getValidatedESNodes(config)) {
        client.addTransportAddress(address);
      }
      return client;
    } catch (RuntimeException e) {
      LOGGER.error("Error initializing Elasticsearch client: {}", e.getMessage(), e);
      if (client != null) {
        client.close();
      }
      throw new ApiException("Error initializing Elasticsearch client: " + e.getMessage(), e);
    }
  }

  protected static TransportClient makeESTransportClient(final ElasticsearchConfiguration config) {
    TransportClient client;
    final String cluster = config.getElasticsearchCluster();
    final String user = config.getUser();
    final String password = config.getPassword();
    final boolean secureClient = StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password);

    final Settings.Builder settings = Settings.builder().put("cluster.name", cluster);
    settings.put("client.transport.sniff", true);

    if (secureClient) {
      LOGGER.info("Enable X-Pack - cluster: {}", cluster);
      settings.put("xpack.security.user", user + ":" + password);
      client = new PreBuiltXPackTransportClient(settings.build());
    } else {
      LOGGER.info("Disable X-Pack - cluster: {}", cluster);
      client = new PreBuiltTransportClient(settings.build());
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
