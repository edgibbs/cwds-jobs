package gov.ca.cwds.jobs.audit;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import gov.ca.cwds.jobs.common.inject.IndexName;
import gov.ca.cwds.jobs.common.util.ConsumerCounter;
import java.util.List;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class AuditEventElasticWriter extends ElasticWriter<AuditEventChangedDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventElasticWriter.class);

  @Inject
  @IndexName
  private String indexName;

  @Inject
  AuditEventElasticWriter(Client client) {
    super(client);
  }

  @Override
  public void write(List<AuditEventChangedDto> items) {
    LOGGER.info("Writing to index [{}]", indexName);
    items.forEach(item -> {
      LOGGER.debug("Preparing to insert/update item: ID {}", item.getId());
      bulkProcessor.add(getBulkService().bulkAdd(item.getId(), item.getDTO()));
    });
    bulkProcessor.flush();
    ConsumerCounter.addToCounter(items.size());
  }

}
