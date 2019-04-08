package gov.ca.cwds.jobs.audit;

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
public class AuditEventsJobModuleBuilder implements JobModuleBuilder {

  @Override
  public JobModule buildJobModule(String[] args) {
    JobOptions jobOptions = JobOptions.parseCommandLine(args);
    AuditEventsJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(AuditEventsJobConfiguration.class,
            jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    ElasticsearchConfiguration elasticsearchConfiguration = jobConfiguration.getElasticsearch();
    elasticsearchConfiguration.setIndexSettings("audit.events.settings.json");
    elasticsearchConfiguration.setDocumentMapping("audit.events.mapping.json");
    JobMode jobMode = getJobMode(jobOptions.getLastRunLoc());
    jobModule
        .addModule(new ElasticSearchModule(elasticsearchConfiguration, jobMode));
    jobModule.addModule(new MultiThreadModule(jobConfiguration.getMultiThread()));
    jobModule.addModule(new AuditEventsJobModule(jobConfiguration, jobMode));
    return jobModule;
  }

  private JobMode getJobMode(String runDir) {
    LocalDateTimeJobModeService timestampJobModeService = new LocalDateTimeJobModeService();
    LocalDateTimeSavePointContainerService savePointContainerService =
        new LocalDateTimeSavePointContainerService(runDir);
    timestampJobModeService.setSavePointContainerService(savePointContainerService);
    return timestampJobModeService.getCurrentJobMode();
  }

}
