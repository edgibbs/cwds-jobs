package gov.ca.cwds.jobs.common.configuration;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public class MultiThreadConfiguration implements JobConfiguration {

  private int batchSize;
  private int elasticSearchBulkSize;
  private int readerThreadsCount;

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getElasticSearchBulkSize() {
    return elasticSearchBulkSize;
  }

  public void setElasticSearchBulkSize(int elasticSearchBulkSize) {
    this.elasticSearchBulkSize = elasticSearchBulkSize;
  }

  public int getReaderThreadsCount() {
    return readerThreadsCount;
  }

  public void setReaderThreadsCount(int readerThreadsCount) {
    this.readerThreadsCount = readerThreadsCount;
  }

}
