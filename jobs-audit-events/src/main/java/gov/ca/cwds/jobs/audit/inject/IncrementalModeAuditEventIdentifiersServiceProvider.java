package gov.ca.cwds.jobs.audit.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.audit.identifier.AuditEventIdentifiersService;
import gov.ca.cwds.jobs.audit.identifier.IncrementalModeAuditEventIdentifiersService;
import gov.ca.cwds.jobs.common.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

public class IncrementalModeAuditEventIdentifiersServiceProvider extends
    AbstractInjectProvider<AuditEventIdentifiersService> {

  @Inject
  public IncrementalModeAuditEventIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<IncrementalModeAuditEventIdentifiersService> getServiceClass() {
    return IncrementalModeAuditEventIdentifiersService.class;
  }

}