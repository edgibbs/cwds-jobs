package gov.ca.cwds.jobs.cals.facility.cws;

import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;

/**
 * Created by Alexander Serbin on 4/5/2018.
 */
public class CustomDb2Dialect extends DB2Dialect {

  private static final AbstractLimitHandler LIMIT_HANDLER = new CwdsJobsLimitHandler();

  @Override
  public LimitHandler getLimitHandler() {
    return LIMIT_HANDLER;
  }
}
