package gov.ca.cwds.jobs.common.entity;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.TestEntityDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class TestEntityService implements ChangedEntityService<TestEntity> {

  @Inject
  private TestEntityDao dao;

  @Override
  @UnitOfWork("test")
  public TestEntity loadEntity(ChangedEntityIdentifier identifier) {
    return dao.find(identifier.getId());
  }

}
