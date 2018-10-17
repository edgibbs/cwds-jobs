package gov.ca.cwds.jobs.cals.facility.cws.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import gov.ca.cwds.jobs.cals.facility.cws.CwsInitialJobModeFinalizer;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class CwsInitialModeFinalizerProvider implements Provider<CwsInitialJobModeFinalizer> {

  @Inject
  private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

  @Inject
  private Injector injector;

  @Override
  public CwsInitialJobModeFinalizer get() {
    CwsInitialJobModeFinalizer service = this.unitOfWorkAwareProxyFactory
        .create(CwsInitialJobModeFinalizer.class);
    this.injector.injectMembers(service);
    return service;
  }

}



