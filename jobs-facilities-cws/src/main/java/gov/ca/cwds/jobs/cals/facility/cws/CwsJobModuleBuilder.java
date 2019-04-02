package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsFacilityJobModule;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobModuleBuilder;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;

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
    JobMode jobMode = getCurrentJobMode(jobOptions.getLastRunLoc());
    jobModule.addModule(new ElasticSearchModule(elasticsearchConfiguration, jobMode));
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    jobModule.addModule(new CwsFacilityJobModule(jobConfiguration, jobMode));
    return jobModule;
  }

  private JobMode getCurrentJobMode(String runDir) {
    LocalDateTimeJobModeService timestampJobModeService =
        new LocalDateTimeJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(runDir);
    timestampJobModeService.setSavePointContainerService(savePointContainerService);
    return timestampJobModeService.getCurrentJobMode();
  }

}
