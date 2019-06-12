package gov.ca.cwds.jobs.audit;

import gov.ca.cwds.jobs.common.core.JobRunner;

/**
 * Created by Alexander Serbin on 12/27/2018
 */
public class AuditEventsJobRunner {

  public static void main(String[] args) {
    AuditEventsJobModuleBuilder jobModuleBuilder = new AuditEventsJobModuleBuilder();
    JobRunner.run(jobModuleBuilder.buildJobModule(args, true));
  }

}
