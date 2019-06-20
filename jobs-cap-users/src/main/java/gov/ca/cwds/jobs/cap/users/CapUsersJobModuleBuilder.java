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

  public static final String REPORT_MODE_FLAG = "-r";

  @Override
  public JobModule buildJobModule(String[] args, boolean elasticSearchModule) {

    boolean isReportMode = hasReportFlag(args);

    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    CapUsersJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(CapUsersJobConfiguration.class, jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setIndexSettings("cap.users.settings.json");
    elasticsearchConfiguration.setDocumentMapping("cap.users.mapping.json");

    JobMode jobMode;
    if(isReportMode) {
      jobMode = JobMode.REPORT;
    } else {
      jobMode = getCurrentJobMode(jobOptions.getLastRunLoc());
      if (elasticSearchModule) {
        jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration, jobMode,
            new CapUsersSavePointContainerService(jobOptions.getLastRunLoc())));
      }
    }

    jobModule.addModule(new CapUsersJobModule(jobConfiguration, jobMode));
    return jobModule;
  }

  private boolean hasReportFlag(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equalsIgnoreCase(REPORT_MODE_FLAG)) {
        System.out.println("!!!REPORT MODE!!!");
        args[i] = "";//to exclude it from Apache CLI paring
        return true;
      }
    }
    return false;
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
