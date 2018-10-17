package gov.ca.cwds.jobs.utils;

import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public final class DataSourceFactoryUtils {

  private DataSourceFactoryUtils() {
  }

  public static void fixDatasourceFactory(DataSourceFactory dataSourceFactory) {
    dataSourceFactory.setUrl(dataSourceFactory.getProperties().get("hibernate.connection.url"));
    dataSourceFactory
        .setUser(dataSourceFactory.getProperties().get("hibernate.connection.username"));
    dataSourceFactory
        .setPassword(dataSourceFactory.getProperties().get("hibernate.connection.password"));
  }

}
