package gov.ca.cwds.jobs.audit;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.audit.inject.NsSessionFactory;
import gov.ca.cwds.jobs.common.core.JobImpl;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class AuditEventsJob extends
    JobImpl<AuditEventChangedDto, TimestampSavePoint<LocalDateTime>, DefaultJobMode> {

  @Inject
  @NsSessionFactory
  private SessionFactory nsSessionFactory;

  @Override
  public void close() {
    super.close();
    nsSessionFactory.close();
  }
}
