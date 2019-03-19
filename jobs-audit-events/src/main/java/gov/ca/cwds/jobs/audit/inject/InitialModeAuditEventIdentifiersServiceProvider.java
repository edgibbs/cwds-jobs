package gov.ca.cwds.jobs.audit.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.audit.identifier.AuditEventIdentifiersService;
import gov.ca.cwds.jobs.audit.identifier.InitialModeAuditEventIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

public class InitialModeAuditEventIdentifiersServiceProvider extends
    AbstractInjectProvider<AuditEventIdentifiersService> {

  @Inject
  public InitialModeAuditEventIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<InitialModeAuditEventIdentifiersService> getServiceClass() {
    return InitialModeAuditEventIdentifiersService.class;
  }

}