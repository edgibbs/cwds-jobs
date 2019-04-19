package gov.ca.cwds.jobs.common.elastic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

public class XPackUtilsTest {

  @Test
  public void testSecureClient() {
    Settings.Builder settings = Settings.builder().put("cluster.name", "elastic-cluster");
    TransportClient result = XPackUtils.secureClient("user", "password", settings);
    assertNotNull(result);
    assertEquals("user:password", settings.get("xpack.security.user"));
    assertEquals("elastic-cluster", settings.get("cluster.name"));
  }
}
