package gov.ca.cwds.jobs.common.elastic;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gov.ca.cwds.rest.api.ApiException;

public class ElasticUtilsTest {

  @Mock
  private ElasticsearchConfiguration configuration;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testCreateAndConfigureESClient() throws IOException {
    doReturn("elasticsearchCluster").when(configuration).getElasticsearchCluster();
    when(configuration.getElasticsearchHost()).thenReturn("localhost");
    when(configuration.getElasticsearchPort()).thenReturn("1010");
    when(configuration.getUser()).thenReturn("user");
    when(configuration.getPassword()).thenReturn("password");
    when(configuration.getUser()).thenReturn("user");
    when(configuration.getPassword()).thenReturn("password");
    List<String> nodes = new ArrayList<>();
    nodes.add("localhost");
    when(configuration.getNodes()).thenReturn(nodes);

    RestHighLevelClient client = ElasticUtils.createAndConfigureESClient(configuration);

    Assert.assertNotNull(client);
  }

  @Test(expected = ApiException.class)
  public void testCreateAndConfigureESClientExceptionThrown() {
    doThrow(RuntimeException.class).when(configuration).getElasticsearchCluster();

    ElasticUtils.createAndConfigureESClient(configuration);
  }

}
