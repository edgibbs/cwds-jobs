package gov.ca.cwds.jobs.common.identifier;

import java.util.List;
import java.util.Optional;

import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 10/11/2018
 */
public interface ChangedEntitiesIdentifiersService<T> {

  Optional<TimestampSavePoint<T>> getNextSavePoint(TimestampSavePoint<T> previousSavePoint);

  Optional<TimestampSavePoint<T>> getFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<T> savePoint);

  List<ChangedEntityIdentifier<TimestampSavePoint<T>>> getIdentifiers(
      Optional<TimestampSavePoint<T>> previousTimestamp,
      Optional<TimestampSavePoint<T>> nextTimestamp);

}
