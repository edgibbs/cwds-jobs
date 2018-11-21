package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsFacilityJobModule;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobModuleBuilder;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;

/**
 * Created by Alexander Serbin on 11/20/2018
 */
public class CwsJobModuleBuilder implements JobModuleBuilder {

  @Override
  public JobModule buildJobModule(String[] args) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    CwsFacilityJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(CwsFacilityJobConfiguration.class,
            jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setDocumentMapping("facility.mapping.json");
    elasticsearchConfiguration.setIndexSettings("facility.settings.json");
    jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration));
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    jobModule.addModule(new CwsFacilityJobModule(jobConfiguration,
        jobOptions.getLastRunLoc()));
    return jobModule;
  }

}
