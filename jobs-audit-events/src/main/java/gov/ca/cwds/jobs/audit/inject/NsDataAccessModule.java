package gov.ca.cwds.jobs.audit.inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.jobs.audit.AuditEventsJobConfiguration;
import gov.ca.cwds.jobs.audit.NsAuditEventDao;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.util.Optional;
import org.hibernate.SessionFactory;

public class NsDataAccessModule extends AbstractModule {

  public static final String NS = "NS";

  private SessionFactory sessionFactory;

  public static final ImmutableList<Class<?>> nsEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          NsAuditEvent.class
      ).build();


  @Override
  protected void configure() {
    bind(NsAuditEventDao.class);
  }

  @Inject
  @Provides
  @NsSessionFactory
  public SessionFactory nsSessionFactory(AuditEventsJobConfiguration jobConfiguration) {
    return getCurrentSessionFactory(jobConfiguration);
  }

  @Provides
  @Inject
  UnitOfWorkAwareProxyFactory provideUnitOfWorkAwareProxyFactory(
      @NsSessionFactory SessionFactory sessionFactory) {
    return new UnitOfWorkAwareProxyFactory(NS, sessionFactory);
  }

  private SessionFactory getCurrentSessionFactory(
      AuditEventsJobConfiguration jobConfiguration) {
    return Optional.ofNullable(sessionFactory).orElseGet(() -> sessionFactory = SessionFactoryUtil
        .buildSessionFactory(jobConfiguration.getNsDataSourceFactory(),
            NS, nsEntityClasses));
  }

}
