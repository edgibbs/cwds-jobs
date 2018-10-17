package gov.ca.cwds.jobs.cals.facility.lisfas;

import gov.ca.cwds.jobs.cals.facility.lisfas.inject.LisFacilityJobModule;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;

/**
 * @author CWDS TPT-2
 */
public final class LisFacilityJobRunner {

  public static void main(String[] args) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    LisFacilityJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(LisFacilityJobConfiguration.class, jobOptions.getLastRunLoc());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setDocumentMapping("facility.mapping.json");
    elasticsearchConfiguration.setIndexSettings("facility.settings.json");
    jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration));
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    jobModule.addModule(new LisFacilityJobModule(jobConfiguration,
        jobOptions.getLastRunLoc()));
    JobRunner.run(jobModule);
  }

}
