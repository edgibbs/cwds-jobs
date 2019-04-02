package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;

/**
 * Created by Alexander Serbin on 4/1/2019
 */
public class ElasticsearchAliasFinalizer implements JobModeFinalizer {

  @Inject
  private ElasticsearchService elasticsearchService;

  @Inject
  @SecondaryFinalizer
  private JobModeFinalizer jobModeFinalizer;

  @Override
  public void doFinalizeJob() {
    jobModeFinalizer.doFinalizeJob();
    elasticsearchService.handleAliases();
  }

}
