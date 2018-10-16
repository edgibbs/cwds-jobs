package gov.ca.cwds.jobs.common.configuration;

import org.apache.commons.cli.Option;

/**
 * Definitions of batch job command line options.
 *
 * @author CWDS API Team
 */
public enum JobCmdLineOption {

  /**
   * ElasticSearch configuration file.
   */
  ES_CONFIG(JobOptions
      .makeOpt("c", JobOptions.CMD_LINE_ES_CONFIG, "ElasticSearch configuration file", true, 1,
          String.class, ',')),

  /**
   * Last run date file (yyyy-MM-dd HH:mm:ss)
   */
  LAST_RUN_FILE(JobOptions
      .makeOpt("l", JobOptions.CMD_LINE_LAST_RUN_FILE, "last run date file (yyyy-MM-dd HH:mm:ss)",
          false, 1, String.class, ','));


  private final Option opt;

  JobCmdLineOption(Option opt) {
    this.opt = opt;
  }

  /**
   * Getter for the type's command line option definition.
   *
   * @return command line option definition
   */
  public final Option getOpt() {
    return opt;
  }

}
