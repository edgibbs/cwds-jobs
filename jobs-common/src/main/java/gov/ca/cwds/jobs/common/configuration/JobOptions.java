package gov.ca.cwds.jobs.common.configuration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.jobs.common.exception.JobsException;

/**
 * Represents batch job options from the command line.
 *
 * @author CWDS API Team
 */
public class JobOptions {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobOptions.class);

  public static final String CMD_LINE_ES_CONFIG = "config";
  public static final String CMD_LINE_LAST_RUN_FILE = "last-run-file";

  /**
   * Location of Elasticsearch configuration file.
   */
  final String esConfigLoc;

  /**
   * Location of last run file.
   */
  final String lastRunLoc;

  public JobOptions(String esConfigLoc, String lastRunLoc) {
    this.esConfigLoc = esConfigLoc;
    this.lastRunLoc = lastRunLoc;
  }

  /**
   * Getter for location of configuration file.
   *
   * @return location of configuration file
   */
  public String getConfigFileLocation() {
    return esConfigLoc;
  }

  /**
   * Getter for location of last run date/time file.
   *
   * @return location of last run file
   */
  public String getLastRunLoc() {
    return lastRunLoc;
  }

  /**
   * Define a command line option.
   *
   * @param shortOpt single letter option name
   * @param longOpt long option name
   * @param description option description
   * @param required true if required
   * @param argc number of arguments to this option
   * @param type arguments' Java class
   * @param sep argument separator
   * @return command line option
   */
  protected static Option makeOpt(String shortOpt, String longOpt, String description,
      boolean required, int argc, Class<?> type, char sep) {
    return Option.builder(shortOpt).argName(longOpt).required(required).longOpt(longOpt)
        .desc(description).numberOfArgs(argc).type(type).valueSeparator(sep).build();
  }

  /**
   * Define command line options.
   *
   * @return command line option definitions
   */
  protected static Options buildCmdLineOptions() {
    final Options ret = new Options();
    ret.addOption(JobCmdLineOption.ES_CONFIG.getOpt());

    // RUN MODE: mutually exclusive choice.
    final OptionGroup group = new OptionGroup();
    group.setRequired(true);
    group.addOption(JobCmdLineOption.LAST_RUN_FILE.getOpt());
    ret.addOptionGroup(group);

    return ret;
  }

  /**
   * Print usage.
   */
  protected static void printUsage() {
    try (final StringWriter sw = new StringWriter()) {
      String equals = StringUtils.leftPad("", 90, '=');
      new HelpFormatter().printHelp(new PrintWriter(sw), 100, "Batch loader",
          equals + "\nUSAGE: java <job class> ...\n" + equals, buildCmdLineOptions(), 4, 8, equals,
          true);
      String usage = sw.toString();
      LOGGER.error(usage);
    } catch (IOException e) {
      final String msg = "ERROR PRINTING HELP! How ironic. :-)";
      LOGGER.error(msg, e);
      throw new JobsException(msg, e);
    }
  }

  /**
   * Parse the command line return the job settings.
   *
   * @param args command line to parse
   * @return JobOptions defining this job
   * @throws JobsException if unable to parse command line
   */
  public static JobOptions parseCommandLine(String[] args) {
    String esConfigLoc = null;
    String lastRunLoc = null;
    try {
      final Options options = buildCmdLineOptions();
      final CommandLineParser parser = new DefaultParser();
      final CommandLine cmd = parser.parse(options, args);

      // Java clincher: case statements only take constants. Even compile-time constants, like
      // enum members (evaluated at compile time), are not considered "constants."
      for (Option opt : cmd.getOptions()) {
        switch (opt.getArgName()) {
          case CMD_LINE_ES_CONFIG:
            esConfigLoc = opt.getValue().trim();
            break;

          case CMD_LINE_LAST_RUN_FILE:
            lastRunLoc = opt.getValue().trim();
            LOGGER.info("last run file = {}", lastRunLoc);
            break;

          default:
            break;
        }
      }
    } catch (ParseException e) {
      printUsage();
      LOGGER.error("Error parsing command line: {}", e.getMessage(), e);
      throw new JobsException("Error parsing command line: " + e.getMessage(), e);
    }

    JobOptions jobOptions = new JobOptions(esConfigLoc, lastRunLoc);
    validateJobOptions(jobOptions);
    return jobOptions;
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") // Path cannot be controlled by the user
  private static String getPathToOutputDirectory(JobOptions jobOptions) {
    return Paths.get(jobOptions.getLastRunLoc()).normalize().toAbsolutePath().toString();
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") // Path cannot be controlled by the user
  private static JobOptions validateJobOptions(JobOptions jobOptions) {
    // check option: -c
    final File configFile = new File(jobOptions.getConfigFileLocation());
    if (!configFile.exists()) {
      throw new JobsException("job arguments error: specified configuration file "
          + configFile.getPath() + " not found");
    }

    // check option: -l
    final File timeFilesDir = new File(jobOptions.getLastRunLoc());
    if (createTimeFilesDirIfMissing(timeFilesDir) && LOGGER.isInfoEnabled()) {
      LOGGER.info("{} was created in file system", getPathToOutputDirectory(jobOptions));
    }

    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Using {} as output folder", getPathToOutputDirectory(jobOptions));
    }
    return jobOptions;
  }

  private static boolean createTimeFilesDirIfMissing(File timeFilesDir) {
    return !timeFilesDir.exists() && timeFilesDir.mkdir();
  }

}
