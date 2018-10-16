package gov.ca.cwds.jobs.common.mode;

import gov.ca.cwds.jobs.common.mode.JobMode;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public interface JobModeService<J extends JobMode> {

  J getCurrentJobMode();

}
