package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Created by Alexander Serbin on 4/1/2019
 */
public class ElasticsearchAliasFinalizerProvider implements Provider<ElasticsearchAliasFinalizer> {

  @Inject
  private ElasticsearchAliasFinalizer elasticsearchAliasFinalizer;

  @Override
  public ElasticsearchAliasFinalizer get() {
    return elasticsearchAliasFinalizer;
  }

}
