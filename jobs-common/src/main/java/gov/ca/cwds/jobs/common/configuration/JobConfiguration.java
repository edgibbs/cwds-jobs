package gov.ca.cwds.jobs.common.configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.jobs.common.exception.JobsException;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public interface JobConfiguration {

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") //Path cannot be controlled by the user
  public static <T extends JobConfiguration> T getJobsConfiguration(Class<T> clazz,
      String path) {
    final String pathToConfiguration = Paths.get(path).toAbsolutePath().toString();
    EnvironmentVariableSubstitutor environmentVariableSubstitutor = new EnvironmentVariableSubstitutor(
        false);
    ConfigurationSourceProvider configurationSourceProvider =
        new SubstitutingSourceProvider(new FileConfigurationSourceProvider(),
            environmentVariableSubstitutor);
    try {
      return new YamlConfigurationFactory<>(
          clazz,
          null,
          Jackson.newObjectMapper(),
          pathToConfiguration).build(configurationSourceProvider, pathToConfiguration);
    } catch (IOException | io.dropwizard.configuration.ConfigurationException e) {
      LoggerFactory.getLogger(JobConfiguration.class)
          .error("Error reading job configuration: {}", e.getMessage(), e);
      throw new JobsException("Error reading job configuration: " + e.getMessage(), e);
    }
  }

}
