package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.jobs.cap.users.service.CapUsersJobModeService;
import gov.ca.cwds.jobs.cap.users.service.CapUsersSavePointContainerService;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobModuleBuilder;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.mode.JobMode;

/**
 * Created by Alexander Serbin on 11/20/2018
 */
public class CapUsersJobModuleBuilder implements JobModuleBuilder {

  @Override
  public JobModule buildJobModule(String[] args, boolean elasticSearchModule) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    CapUsersJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(CapUsersJobConfiguration.class, jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setIndexSettings("cap.users.settings.json");
    elasticsearchConfiguration.setDocumentMapping("cap.users.mapping.json");
    JobMode jobMode = getCurrentJobMode(jobOptions.getLastRunLoc());
    if (elasticSearchModule) {
      jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration, jobMode));
    }
    jobModule.addModule(new CapUsersJobModule(jobConfiguration, jobMode));
    return jobModule;
  }

  private JobMode getCurrentJobMode(String runDir) {
    CapUsersJobModeService capUsersJobModeService =
        new CapUsersJobModeService();
    CapUsersSavePointContainerService savePointContainerService =
        new CapUsersSavePointContainerService(runDir);
    capUsersJobModeService.setSavePointContainerService(savePointContainerService);
    return capUsersJobModeService.getCurrentJobMode();
  }


}
