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
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public interface JobConfiguration {

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") //Path cannot be controlled by the user
  static <T extends JobConfiguration> T getJobsConfiguration(Class<T> clazz,
      String path) {
    final String pathToConfiguration = Paths.get(path).toAbsolutePath().toString();
    EnvironmentVariableSubstitutor environmentVariableSubstitutor = new EnvironmentVariableSubstitutor(
        false);
    ConfigurationSourceProvider configurationSourceProvider =
        new SubstitutingSourceProvider(new FileConfigurationSourceProvider(),
            environmentVariableSubstitutor);
    try {
      T configuration = new YamlConfigurationFactory<>(
          clazz,
          null,
          Jackson.newObjectMapper(),
          pathToConfiguration).build(configurationSourceProvider, pathToConfiguration);
      handleValidationError(configuration);
      return configuration;
    } catch (IOException | io.dropwizard.configuration.ConfigurationException e) {
      LoggerFactory.getLogger(JobConfiguration.class)
          .error("Error reading job configuration: {}", e.getMessage(), e);
      throw new JobsException("Error reading job configuration: " + e.getMessage(), e);
    }
  }

  static <T extends JobConfiguration> void handleValidationError(T configuration) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<T>> errors = validator.validate(configuration);
    if (!errors.isEmpty()) {
      String message = errors.stream().map(c->"[" + c.getPropertyPath() + "] " + c.getMessage())
          .collect(Collectors.joining(", "));
      throw new JobsException(String.format("Configuration validation issues: %s", message));
    }
  }

}
