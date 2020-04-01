package gov.ca.cwds.jobs.cals.facility.cws;

import java.time.LocalDateTime;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.core.JobImpl;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class CwsFacilityJob extends JobImpl<ChangedFacilityDto, TimestampSavePoint<LocalDateTime>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsFacilityJob.class);

  @Inject
  @CmsSessionFactory
  private SessionFactory cmsSessionFactory;

  @Inject
  @CalsnsSessionFactory
  private SessionFactory calsnsSessionFactory;

  @Override
  public void close() {
    LOGGER.info("Closing job Facility CWS ...");
    super.close();
    cmsSessionFactory.close();
    calsnsSessionFactory.close();
    LOGGER.info("Job closed");
  }

}
