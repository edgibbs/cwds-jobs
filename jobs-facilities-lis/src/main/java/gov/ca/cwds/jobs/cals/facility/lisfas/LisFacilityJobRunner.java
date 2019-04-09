package gov.ca.cwds.jobs.cals.facility.lisfas;

import gov.ca.cwds.jobs.common.core.JobRunner;

/**
 * @author CWDS TPT-2
 */
public final class LisFacilityJobRunner {

  public static void main(String[] args) {
    LisJobModuleBuilder jobModuleBuilder = new LisJobModuleBuilder();
    JobRunner.run(jobModuleBuilder.buildJobModule(args, true));
  }

}
