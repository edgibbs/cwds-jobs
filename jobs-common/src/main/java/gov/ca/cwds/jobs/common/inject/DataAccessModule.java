package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import io.dropwizard.db.DataSourceFactory;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/18/2019
 */
public abstract class DataAccessModule extends AbstractModule {

  private SessionFactory sessionFactory;

  public DataAccessModule(DataSourceFactory dataSourceFactory,
      String dataSourceName, List<Class<?>> entityClasses) {
    try {
      sessionFactory = SessionFactoryUtil
          .buildSessionFactory(dataSourceFactory, dataSourceName, entityClasses);
    } catch (Exception e) {
      throw new JobsException(
          String.format("Couldn't build session factory %s", dataSourceName), e);
    }
  }

  protected SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}
