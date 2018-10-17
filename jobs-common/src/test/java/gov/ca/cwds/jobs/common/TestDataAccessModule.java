package gov.ca.cwds.jobs.common;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.ca.cwds.jobs.common.entity.TestEntity;
import gov.ca.cwds.jobs.common.inject.TestSessionFactory;
import gov.ca.cwds.jobs.common.util.SessionFactoryUtil;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class TestDataAccessModule extends AbstractModule {

  public static final ImmutableList<Class<?>> testEntityClasses = ImmutableList.<Class<?>>builder()
      .add(
          TestEntity.class
      ).build();

  @Override
  protected void configure() {
    bind(SessionFactory.class).annotatedWith(TestSessionFactory.class)
        .toProvider(TestSessionFactoryProvider.class).in(Singleton.class);
  }

  private static class TestSessionFactoryProvider implements Provider<SessionFactory> {

    @Inject
    private TestJobConfiguration configuration;

    @Override
    public SessionFactory get() {
      return SessionFactoryUtil
          .buildSessionFactory(configuration.getTestDataSourceFactory(),
              "test", testEntityClasses);
    }
  }

}
