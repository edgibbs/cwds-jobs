package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.core.JobPreparator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class JobModule<T extends JobConfiguration> extends AbstractModule {

  private String lastRunDir;

  private JobPreparator jobPreparator = ()->{};

  private List<AbstractModule> modules = new ArrayList<>();

  private T jobConfiguration;

  public JobModule(T jobsConfiguration, String lastRunDir) {
    this.jobConfiguration = jobsConfiguration;
    this.lastRunDir = lastRunDir;
  }

  public void setJobPreparator(JobPreparator jobPreparator) {
    this.jobPreparator = jobPreparator;
  }

  public void setJobConfiguration(T jobConfiguration) {
    this.jobConfiguration = jobConfiguration;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(LastRunDir.class).to(lastRunDir);
    bind(JobPreparator.class).toInstance(jobPreparator);
    modules.forEach(this::install);
  }

  public void addModules(AbstractModule ... modules) {
    for (AbstractModule module: modules) {
      this.modules.add(module);
    }
  }

  static class DefaultJobPreparator implements JobPreparator {

    @Override
    public void run() {
      //empty by default
    }
  }
}
