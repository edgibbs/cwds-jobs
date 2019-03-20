package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.FasFfaSessionFactory;
import gov.ca.cwds.cals.inject.FasSessionFactory;
import gov.ca.cwds.cals.persistence.model.fas.LpaInformation;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class FasDataAccessModule extends DataAccessModule {

  public static final ImmutableList<Class<?>> fasEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
//          RecordChange.class,
          LpaInformation.class
      ).build();

  public FasDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, DataSourceName.FAS.name(), fasEntityClasses);
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(FasSessionFactory.class)
        .toInstance(getSessionFactory());
    bind(SessionFactory.class).annotatedWith(FasFfaSessionFactory.class)
        .toInstance(getSessionFactory());
  }

}
