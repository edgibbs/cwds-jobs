package gov.ca.cwds.jobs.common.dao;

import static java.util.Objects.requireNonNull;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Ancestor for all the custom dao.
 *
 * @author Alex Serbin
 */
public abstract class CustomDao {

  private final SessionFactory sessionFactory;

  public CustomDao(SessionFactory sessionFactory) {
    this.sessionFactory = requireNonNull(sessionFactory);
  }

  /**
   * Current hibernate session to use in decsendants.
   */
  protected Session currentSession() {
    return sessionFactory.getCurrentSession();
  }
}
