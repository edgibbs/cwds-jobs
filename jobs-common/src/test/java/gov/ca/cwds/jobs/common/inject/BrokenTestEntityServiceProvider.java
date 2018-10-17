package gov.ca.cwds.jobs.common.inject;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import gov.ca.cwds.jobs.common.entity.BrokenTestEntityService;
import gov.ca.cwds.jobs.common.entity.TestEntityService;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class BrokenTestEntityServiceProvider implements Provider<TestEntityService> {

  @Inject
  private UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory;

  @Inject
  private Injector injector;

  @Override
  public TestEntityService get() {
    TestEntityService service = this.unitOfWorkAwareProxyFactory
        .create(BrokenTestEntityService.class);
    this.injector.injectMembers(service);
    return service;
  }
}



