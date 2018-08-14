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
  List<ChangedEntityIdentifier<T>> getIdentifiers(T savePoint, PageRequest pageRequest);

  /**
   * Fetches the first changed timestamp savePoint after the savePoint.
   * @param savePoint savePoint
   * @return savePoint
   */
  T getFirstChangedTimestamp(T savePoint);

  /**
   * Fetches all identifiers before the first changed timestamp savePoint using offset.
   * @param timestampSavePoint first changed timestamp savePoint
   * @param offset offset
   * @return list of identifiers before given savePoint
   */
  List<ChangedEntityIdentifier<T>> getIdentifiersBeforeChangedTimestamp(
      T timestampSavePoint, int offset);
}
