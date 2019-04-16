package gov.ca.cwds.jobs.common.elastic;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import gov.ca.cwds.rest.api.ApiException;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ElasticUtilsTest {

  @Mock private ElasticsearchConfiguration configuration;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCreateAndConfigureESClient() {
    doReturn("elasticsearchCluster").when(configuration).getElasticsearchCluster();
    when(configuration.getElasticsearchHost()).thenReturn("localhost");
    when(configuration.getElasticsearchPort()).thenReturn("1010");

    TransportClient client = ElasticUtils.createAndConfigureESClient(configuration);

    assertEquals("localhost", client.listedNodes().get(0).getAddress().getHost());
    assertEquals(1010, client.listedNodes().get(0).getAddress().getPort());
    assertEquals("transport", client.settings().get("client.type"));
    assertEquals("elasticsearchCluster", client.settings().get("cluster.name"));
    assertEquals("security4", client.settings().get("http.type"));
    assertEquals("security4", client.settings().get("transport.type"));
  }

  @Test(expected = ApiException.class)
  public void testCreateAndConfigureESClientExceptionThrown() {
    doThrow(RuntimeException.class).when(configuration).getElasticsearchCluster();

    ElasticUtils.createAndConfigureESClient(configuration);
  }
}
