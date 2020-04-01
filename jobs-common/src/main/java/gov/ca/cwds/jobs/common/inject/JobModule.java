package gov.ca.cwds.jobs.common.inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.inject.AbstractModule;

import gov.ca.cwds.jobs.common.core.JobPreparator;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
public class JobModule extends AbstractModule {

  private String lastRunDir;

  private JobPreparator jobPreparator = () -> {
  };

  private List<AbstractModule> modules = new ArrayList<>();

  public JobModule(String lastRunDir) {
    this.lastRunDir = lastRunDir;
  }

  public void setJobPreparator(JobPreparator jobPreparator) {
    this.jobPreparator = jobPreparator;
  }

  @Override
  protected void configure() {
    bindConstant().annotatedWith(LastRunDir.class).to(lastRunDir);
    bind(JobPreparator.class).toInstance(jobPreparator);
    modules.forEach(this::install);
  }

  public void addModules(AbstractModule... modules) {
    this.modules.addAll(Arrays.asList(modules));
  }

  public void addModule(AbstractModule module) {
    this.modules.add(module);
  }

  public List<AbstractModule> getModules() {
    return modules;
  }

  static class DefaultJobPreparator implements JobPreparator {

    @Override
    public void run() {
      // empty by default
    }
  }

}
