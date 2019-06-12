package gov.ca.cwds.jobs.cap.users;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cap.users.dao.CwsUsersDao;
import gov.ca.cwds.jobs.cap.users.entity.CwsOffice;
import gov.ca.cwds.jobs.cap.users.entity.StaffPerson;
import gov.ca.cwds.jobs.cap.users.entity.UserId;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

public class CwsCmsDataAccessModule extends DataAccessModule {

  public static final String CWS = "CWS";

  public static final ImmutableList<Class<?>> cwsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          CwsOffice.class,
          StaffPerson.class,
          UserId.class
      ).build();

  public CwsCmsDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, CWS, cwsEntityClasses);
  }

  @Override
  protected void configure() {
    bind(CwsUsersDao.class);
  }

  @Provides
  @CmsSessionFactory
  public SessionFactory cmsSessionFactory() {
    return getSessionFactory();
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @CmsSessionFactory SessionFactory sessionFactory) {
    return new UnitOfWorkAwareProxyFactory(CWS, sessionFactory);
  }

}
