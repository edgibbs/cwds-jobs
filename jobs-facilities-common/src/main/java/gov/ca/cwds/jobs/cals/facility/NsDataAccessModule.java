package gov.ca.cwds.jobs.cals.facility;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.cals.persistence.model.calsns.dictionaries.LicenseStatusType;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * Created by Ievgenii Drozd on 4/30/2018.
 */
public class NsDataAccessModule extends DataAccessModule {

  public static final ImmutableList<Class<?>> nsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          gov.ca.cwds.cals.persistence.model.calsns.dictionaries.FacilityType.class,
          LicenseStatusType.class
      ).build();

  public NsDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, DataSourceName.NS.name(), nsEntityClasses);
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(CalsnsSessionFactory.class)
        .toInstance(getSessionFactory());
  }

}