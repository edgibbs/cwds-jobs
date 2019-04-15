package gov.ca.cwds.jobs.common.inject;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import io.dropwizard.db.DataSourceFactory;
import java.util.List;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/18/2019
 */
public abstract class DataAccessModule extends AbstractModule {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessModule.class);

  private SessionFactory sessionFactory;

  @SuppressWarnings({"squid:S1147", "findbugs:DM_EXIT"}) // suppressing "Exit methods should not be called" since we really need it here
  public DataAccessModule(DataSourceFactory dataSourceFactory,
      String dataSourceName, List<Class<?>> entityClasses) {
    try {
      sessionFactory = SessionFactoryUtil
          .buildSessionFactory(dataSourceFactory, dataSourceName, entityClasses);
    } catch (Exception e) {
      LOGGER.error(String.format("Couldn't build session factory %s", dataSourceName), e);
      //This is workaround to address hanging. Temporary solution that needs to be replaced
      //by permanent one
      System.exit(1);
    }
  }

  protected SessionFactory getSessionFactory() {
    return sessionFactory;
  }

}
