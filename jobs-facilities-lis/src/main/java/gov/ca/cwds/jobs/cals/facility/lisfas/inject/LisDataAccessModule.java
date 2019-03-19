package gov.ca.cwds.jobs.cals.facility.lisfas.inject;

import com.google.common.collect.ImmutableList;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.persistence.dao.lis.LisFacFileLisDao;
import gov.ca.cwds.cals.persistence.dao.lis.LisTableFileDao;
import gov.ca.cwds.cals.persistence.model.lisfas.LisDoFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisFacFile;
import gov.ca.cwds.cals.persistence.model.lisfas.LisTableFile;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.LicenseNumberIdentifierDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisTimestampIdentifier;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class LisDataAccessModule extends DataAccessModule {

  public static final ImmutableList<Class<?>> lisEntityClasses =
      ImmutableList.<Class<?>>builder().add(
          LicenseNumberIdentifier.class,
          LisTimestampIdentifier.class,
          LisFacFile.class,
          LisTableFile.class,
          LisDoFile.class
      ).build();

  public LisDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, DataSourceName.LIS.name(), lisEntityClasses);
  }

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(LisSessionFactory.class)
        .toInstance(getSessionFactory());
    bind(LicenseNumberIdentifierDao.class);
    bind(LisFacFileLisDao.class);
    bind(LisTableFileDao.class);
  }


}
