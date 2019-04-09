package gov.ca.cwds.jobs.cals.facility.lisfas;

import gov.ca.cwds.jobs.cals.facility.lisfas.inject.LisFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainerService;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobModuleBuilder;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import gov.ca.cwds.jobs.common.mode.JobMode;

/**
 * Created by Alexander Serbin on 11/20/2018
 */
public class LisJobModuleBuilder implements JobModuleBuilder {

  @Override
  public JobModule buildJobModule(String[] args, boolean elasticSearchModule) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    LisFacilityJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(LisFacilityJobConfiguration.class,
            jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setDocumentMapping("facility.mapping.json");
    elasticsearchConfiguration.setIndexSettings("facility.settings.json");
    JobMode jobMode = getCurrentJobMode(jobOptions.getLastRunLoc());
    if (elasticSearchModule) {
      jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration, jobMode));
    }
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    jobModule.addModule(new LisFacilityJobModule(jobConfiguration, jobMode));
    return jobModule;
  }

  private JobMode getCurrentJobMode(String runDir) {
    LisJobModeService timestampJobModeService =
        new LisJobModeService();
    LicenseNumberSavePointContainerService savePointContainerService =
        new LicenseNumberSavePointContainerService(runDir);
    timestampJobModeService.setSavePointContainerService(savePointContainerService);
    return timestampJobModeService.getCurrentJobMode();
  }


}
