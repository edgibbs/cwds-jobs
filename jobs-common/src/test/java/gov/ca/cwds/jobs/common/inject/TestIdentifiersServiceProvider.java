package gov.ca.cwds.jobs.common.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import gov.ca.cwds.jobs.common.identifier.TestIdentifiersService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class TestIdentifiersServiceProvider implements Provider<TestIdentifiersService> {

  @Inject
  private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

  @Inject
  private Injector injector;

  @Override
  public TestIdentifiersService get() {
    TestIdentifiersService service = this.unitOfWorkAwareProxyFactory
        .create(TestIdentifiersService.class);
    this.injector.injectMembers(service);
    return service;
  }
}



