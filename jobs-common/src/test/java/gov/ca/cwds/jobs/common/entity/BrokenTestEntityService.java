package gov.ca.cwds.jobs.common.entity;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.TestEntityDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class BrokenTestEntityService extends TestEntityService {

  public static final String BROKEN_ENTITY_ID = "broken";

  @Inject
  private TestEntityDao dao;

  @Override
  @UnitOfWork("test")
  public TestEntity loadEntity(ChangedEntityIdentifier identifier) {
    if (identifier.getId().equals(BROKEN_ENTITY_ID)) {
      throw new RuntimeException("Broken entity!!!");
    }
    return super.loadEntity(identifier);
  }

}
