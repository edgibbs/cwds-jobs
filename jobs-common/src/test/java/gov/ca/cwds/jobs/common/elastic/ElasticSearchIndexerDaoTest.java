package gov.ca.cwds.jobs.common.elastic;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import gov.ca.cwds.jobs.common.configuration.MultiThreadJobConfiguration;
import java.io.IOException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * Created by Ievgenii Drozd on 5/3/2018.
 */
@Ignore
public class ElasticSearchIndexerDaoTest {

  private static final String ES_ALIAS = "mockESAlias";
  private static final String DOC_TYPE = "mockDockType";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private RestHighLevelClient clientMock;

  @Mock
  private ElasticsearchConfiguration configMock;

  @Mock
  private AdminClient adminClientMock;

  @Mock
  private ClusterAdminClient clusterAdminClientMock;

  @Mock
  private ActionFuture actionFutureMock;

  @Mock
  private ClusterStateResponse clusterStateResponseMock;

  @Mock
  private ClusterState clusterStateMock;

  @Mock
  private MetaData metaDataMock;

  @Mock
  private IndexMetaData indexMetaDataMock;

  @Mock
  private IndicesAdminClient indicesAdminClientMock;

  @Mock
  private CreateIndexRequestBuilder createIndexRequestBuilder;

  @Mock
  private ActionFuture createActionMock;

  @Spy
  @InjectMocks
  private ElasticSearchIndexerDao indexerDao; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() {
    assertThat(ElasticSearchIndexerDao.class, notNullValue());
  }

  @Test
  public void instantiation() {
    assertThat(indexerDao, notNullValue());
  }

  @Test
  public void testConstructorWithParameters() {
    indexerDao = new ElasticSearchIndexerDao(clientMock, configMock);
  }

  @Test
  public void testIndexDoesExist() {
    setUpClusterMock();
    when(metaDataMock.index(ES_ALIAS)).thenReturn(indexMetaDataMock); //index should exist
    indexerDao.createIndexIfMissing();
  }

  @Test
  public void testIndexDoesNotExist() {
    setUpClusterMock();
    when(metaDataMock.index(ES_ALIAS)).thenReturn(null); //empty index
    indexerDao.createIndexIfMissing();
  }

  @Test
  public void testCreateIndex() {
    setUpClusterMock();
    when(metaDataMock.index(ES_ALIAS)).thenReturn(null); //empty index

    indexerDao.createIndexIfMissing();
  }

  @Test
  public void testCloseClient() throws IOException {
    setUpClusterMock();
    indexerDao = new ElasticSearchIndexerDao(null, configMock);//empty client
    indexerDao.close();

    indexerDao = new ElasticSearchIndexerDao(clientMock, configMock);// non-empty client
    indexerDao.close();
  }

  @Test(expected = IOException.class)
  public void testCloseClientWithException() throws IOException {
    setUpClusterMock();

    doAnswer(invocation -> {
      throw new Exception();
    }).when(clientMock).close();
    indexerDao = new ElasticSearchIndexerDao(clientMock, configMock);
    indexerDao.close();
  }

  @Test
  public void testGetClient() {
    setUpClusterMock();
    Assert.assertEquals(clientMock, indexerDao.getClient());
  }

  @SuppressWarnings("unchecked")
  private void setUpClusterMock() {
    when(clusterStateMock.getMetaData()).thenReturn(metaDataMock);
    when(clusterStateResponseMock.getState()).thenReturn(clusterStateMock);
    when(actionFutureMock.actionGet()).thenReturn(clusterStateResponseMock);
    when(clusterAdminClientMock.state(Mockito.any())).thenReturn(actionFutureMock);
    when(adminClientMock.cluster()).thenReturn(clusterAdminClientMock);
    when(configMock.getElasticsearchAlias()).thenReturn(ES_ALIAS);
    when(configMock.getElasticsearchDocType()).thenReturn(DOC_TYPE);
//    when(clientMock.admin()).thenReturn(adminClientMock);
    when(adminClientMock.indices()).thenReturn(indicesAdminClientMock);
    when(indicesAdminClientMock.prepareCreate(ES_ALIAS)).thenReturn(createIndexRequestBuilder);
    when(createIndexRequestBuilder.request()).thenReturn(null);
    when(indicesAdminClientMock.create(Mockito.any())).thenReturn(createActionMock);
//    when(clientMock
//        .prepareDelete(Mockito.any(), Mockito.any(), Mockito.any()))
//        .thenThrow(Exception.class);
    doAnswer(invocation -> null).when(createActionMock).actionGet();
    doAnswer(invocation -> null).when(createIndexRequestBuilder)
        .setSettings(Mockito.any(), Mockito.any());
    doAnswer(invocation -> null).when(createIndexRequestBuilder)
        .addMapping(Mockito.any(), Mockito.any(), Mockito.any());

    indexerDao = new ElasticSearchIndexerDao(clientMock, configMock);
    assertThat(indexerDao.getClient(), notNullValue());
  }
}
