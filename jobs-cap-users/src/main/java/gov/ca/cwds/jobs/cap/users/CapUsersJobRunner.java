package gov.ca.cwds.jobs.cap.users;


import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;

/**
 * @author CWDS TPT-3
 */
public final class CapUsersJobRunner {

  public static void main(String[] args) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    CapUsersJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(CapUsersJobConfiguration.class, jobOptions.getLastRunLoc());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setIndexSettings("cap.users.settings.json");
    elasticsearchConfiguration.setDocumentMapping("cap.users.mapping.json");
    jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration));
    jobModule.addModule(new CapUsersJobModule(jobConfiguration,
        jobOptions.getLastRunLoc()));
    JobRunner.run(jobModule);
  }
}
