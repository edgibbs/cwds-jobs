package gov.ca.cwds.jobs.audit.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.jobs.audit.AuditInitialJobModeFinalizer;
import gov.ca.cwds.jobs.common.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

public class AuditInitialJobModeFinalizerProvider extends
    AbstractInjectProvider<AuditInitialJobModeFinalizer> {

  @Inject
  public AuditInitialJobModeFinalizerProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<AuditInitialJobModeFinalizer> getServiceClass() {
    return AuditInitialJobModeFinalizer.class;
  }

}
