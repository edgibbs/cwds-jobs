package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.Inject;
import com.google.inject.Injector;
import gov.ca.cwds.cals.inject.AbstractInjectProvider;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class ChangedRFAIdentifiersProvider extends
    AbstractInjectProvider<ChangedRfaFormIdentifiersService> {

  @Inject
  public ChangedRFAIdentifiersProvider(Injector injector,
      UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory) {
    super(injector, unitOfWorkAwareProxyFactory);
  }

  @Override
  public Class<ChangedRfaFormIdentifiersService> getServiceClass() {
    return ChangedRfaFormIdentifiersService.class;
  }
}
