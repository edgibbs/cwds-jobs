package gov.ca.cwds.jobs.audit.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.jobs.audit.NsAuditEventDao;
import gov.ca.cwds.jobs.common.inject.DataAccessModule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import org.hibernate.SessionFactory;

public class NsDataAccessModule extends DataAccessModule {

  public static final String NS = "NS";

  public static final ImmutableList<Class<?>> nsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          NsAuditEvent.class
      ).build();

  public NsDataAccessModule(DataSourceFactory dataSourceFactory) {
    super(dataSourceFactory, NS, nsEntityClasses);
  }

  @Override
  protected void configure() {
    bind(NsAuditEventDao.class);
  }

  @Provides
  @NsSessionFactory
  public SessionFactory nsSessionFactory() {
    return getSessionFactory();
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @NsSessionFactory SessionFactory sessionFactory) {
    return new UnitOfWorkAwareProxyFactory(NS, sessionFactory);
  }

}
