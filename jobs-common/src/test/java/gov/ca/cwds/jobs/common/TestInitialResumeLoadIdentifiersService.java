package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;

/** Created by Alexander Serbin on 3/30/2018. */
public class TestInitialResumeLoadIdentifiersService extends TestIncrementalLoadIdentifiersService {

  public TestInitialResumeLoadIdentifiersService(
      List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers) {
    super(identifiers);
  }
}
