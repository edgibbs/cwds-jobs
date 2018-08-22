package gov.ca.cwds.jobs.common.identifier;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.List;

/**
 * This service provides next identifiers pages for every job mode.
 * Savepoint can be of different nature depending on job mode.
 * For example, value of incremental field for initial job and timestamp for
 * incremental job
 * Created by Alexander Serbin on 3/5/2018.
 */
public interface ChangedEntitiesIdentifiersService<T extends SavePoint> {

  /**
   * Fetches next page of target entities' identifiers for initial load.
   */
  List<ChangedEntityIdentifier<T>> getIdentifiersForInitialLoad(PageRequest pageRequest);

  /**
   * Fetches next page of target entities' identifiers for resuming initial load.
   */
  List<ChangedEntityIdentifier<T>> getIdentifiersForResumingInitialLoad(T savePoint,
      PageRequest pageRequest);

  /**
   * Fetches next page of target entities' identifiers for incremental load.
   */
  List<ChangedEntityIdentifier<T>> getIdentifiersForIncrementalLoad(T savePoint,
      PageRequest pageRequest);

}
