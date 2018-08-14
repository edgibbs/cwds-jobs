package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;

/** Created by Alexander Serbin on 3/30/2018. */
public class TestIncrementalLoadIdentifiersService extends AbstractTestChangedIdentifiersService {

  public TestIncrementalLoadIdentifiersService(
      List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers) {
    super(identifiers);
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      TimestampSavePoint<LocalDateTime> savePoint, PageRequest pageRequest) {
    return getNextPage(pageRequest, getFilteredIdentifiers(savePoint));
  }
}
