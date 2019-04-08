package gov.ca.cwds.jobs.cals.facility.cws;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.core.JobImpl;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class CwsFacilityJob extends
    JobImpl<ChangedFacilityDto, TimestampSavePoint<LocalDateTime>> {

  @Inject
  @CmsSessionFactory
  private SessionFactory cmsSessionFactory;

  @Inject
  @CalsnsSessionFactory
  private SessionFactory calsnsSessionFactory;

  @Override
  public void close() {
    super.close();
    cmsSessionFactory.close();
    calsnsSessionFactory.close();
  }
}
