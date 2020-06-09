package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;

import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * @author CWDS TPT-2
 */

public class LisChangedIdentifiersServiceProvider
    extends AbstractInjectProvider<LisChangedEntitiesIdentifiersService> {

  @Inject
  public LisChangedIdentifiersServiceProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<LisChangedEntitiesIdentifiersService> getServiceClass() {
    return LisChangedEntitiesIdentifiersService.class;
  }

}
