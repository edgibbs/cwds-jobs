package gov.ca.cwds.jobs.audit;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.core.Job;

public class AuditEventsJobModule extends AbstractModule {

  private AuditEventsJobConfiguration configuration;

  public AuditEventsJobModule(AuditEventsJobConfiguration jobConfiguration) {
    this.configuration = jobConfiguration;
  }

  public AuditEventsJobConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  protected void configure() {
    bind(Job.class).toInstance(() -> {
    });
  }

}

