package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.common.core.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class CwsFacilityJobRunner {

  public static void main(String[] args) {
    CwsJobModuleBuilder jobModuleBuilder = new CwsJobModuleBuilder();
    JobRunner.run(jobModuleBuilder.buildJobModule(args, true));
  }

}
