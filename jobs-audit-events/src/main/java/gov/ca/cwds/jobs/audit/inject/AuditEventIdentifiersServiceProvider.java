package gov.ca.cwds.jobs.audit.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.audit.identifier.AuditEventIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

public class AuditEventIdentifiersServiceProvider extends
    AbstractInjectProvider<AuditEventIdentifiersService> {

  @Inject
  public AuditEventIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<AuditEventIdentifiersService> getServiceClass() {
    return AuditEventIdentifiersService.class;
  }

}
