package gov.ca.cwds.jobs.common.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public final class TimeSpentUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimeSpentUtil.class);

  private TimeSpentUtil() {}

  public static void printTimeSpent(String workDescription, LocalDateTime startTime) {
    long hours = startTime.until(LocalDateTime.now(), ChronoUnit.HOURS);
    long minutes = startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
    long seconds = startTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
    long milliseconds = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);

    LOGGER.info("{} time spent in milliseconds - {} ms", workDescription, milliseconds);
    if (hours > 0) {
      LOGGER.info("{} time spent in hours - {} hr", workDescription, hours);
    } else if (minutes > 0) {
      LOGGER.info("{} time spent in minutes - {} min", workDescription, minutes);
    } else if (seconds > 0) {
      LOGGER.info("{} time spent in seconds - {} sec", workDescription, seconds);
    }
  }

}
