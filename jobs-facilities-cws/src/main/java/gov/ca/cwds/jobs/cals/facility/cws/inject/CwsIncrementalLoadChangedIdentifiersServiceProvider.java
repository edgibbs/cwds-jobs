package gov.ca.cwds.jobs.cals.facility.cws.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsIncrementalLoadChangedEntitiesIdentifiersService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/** @author CWDS TPT-2 */
public class CwsIncrementalLoadChangedIdentifiersServiceProvider
    extends AbstractInjectProvider<CwsIncrementalLoadChangedEntitiesIdentifiersService> {

  @Inject
  public CwsIncrementalLoadChangedIdentifiersServiceProvider(
      Injector injector, UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<CwsIncrementalLoadChangedEntitiesIdentifiersService> getServiceClass() {
    return CwsIncrementalLoadChangedEntitiesIdentifiersService.class;
  }
}
