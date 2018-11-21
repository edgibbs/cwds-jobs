package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobModuleBuilder;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;

/**
 * Created by Alexander Serbin on 11/20/2018
 */
public class CapUsersJobModuleBuilder implements JobModuleBuilder {

  @Override
  public JobModule buildJobModule(String[] args) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    CapUsersJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(CapUsersJobConfiguration.class, jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setIndexSettings("cap.users.settings.json");
    elasticsearchConfiguration.setDocumentMapping("cap.users.mapping.json");
    jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration));
    jobModule.addModule(new CapUsersJobModule(jobConfiguration,
        jobOptions.getLastRunLoc()));
    return jobModule;
  }

}
