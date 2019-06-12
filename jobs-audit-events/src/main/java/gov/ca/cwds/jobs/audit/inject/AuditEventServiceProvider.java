package gov.ca.cwds.jobs.audit.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.audit.AuditEventService;
import gov.ca.cwds.jobs.common.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

public class AuditEventServiceProvider extends AbstractInjectProvider<AuditEventService> {

  @Inject
  public AuditEventServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<AuditEventService> getServiceClass() {
    return AuditEventService.class;
  }
}
