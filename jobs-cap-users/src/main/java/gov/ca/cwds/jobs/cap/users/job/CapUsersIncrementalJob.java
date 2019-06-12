package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cap.users.dto.CapJobResult;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersIncrementalJob extends AbstractCapUsersJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersIncrementalJob.class);

  @Inject
  @CmsSessionFactory
  private SessionFactory cmsSessionFactory;

  @Inject
  private CapUsersIncrementalUpdatesProcessor updatesProcessor;

  @Override
  CapJobResult runJob() {
    LOGGER.info("CapUsersIncrementalJob is running");
    CapJobResult result =  updatesProcessor.processUpdates();
    LOGGER.info("Finishing Incremental Cap Users Job");
    return result;
  }

  @Override
  public void close() {
    super.close();
    cmsSessionFactory.close();
    updatesProcessor.destroy();
  }
}
