package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public abstract class AbstractTestChangedIdentifiersService
    implements ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> {

  private List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers;

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers() {
    return identifiers;
  }

  public AbstractTestChangedIdentifiersService(
      List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers) {
    this.identifiers = identifiers;
  }

  @Override
  public TimestampSavePoint<LocalDateTime> getFirstChangedTimestamp(
      TimestampSavePoint<LocalDateTime> timestampSavePoint) {
    LocalDateTime lastTimestamp = timestampSavePoint.getTimestamp();
    for (ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> identifier : identifiers) {
      LocalDateTime firstChangedTimestamp = identifier.getSavePoint().getTimestamp();
      if (firstChangedTimestamp != null && lastTimestamp == null) {
        return new LocalDateTimeSavePoint(firstChangedTimestamp);
      }
      if (firstChangedTimestamp != null && firstChangedTimestamp.isAfter(lastTimestamp)) {
        return new LocalDateTimeSavePoint(firstChangedTimestamp);
      }
    }
    return null;
  }

  @Override
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
      getIdentifiersBeforeChangedTimestamp(
          TimestampSavePoint<LocalDateTime> timestampSavePoint, int offset) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
        identifiersBeforeChangedTimestamp = new ArrayList<>();
    LocalDateTime firstChangedTimestamp = timestampSavePoint.getTimestamp();

    for (int i = offset; i < identifiers.size(); i++) {
      if (firstChangedTimestamp == null
          && identifiers.get(i).getSavePoint().getTimestamp() != null) {
        identifiersBeforeChangedTimestamp.add(identifiers.get(i));
      }
      if (firstChangedTimestamp != null
          && (identifiers.get(i).getSavePoint().getTimestamp() == null
            || identifiers.get(i).getSavePoint().getTimestamp().isBefore(firstChangedTimestamp))) {
        identifiersBeforeChangedTimestamp.add(identifiers.get(i));
      }
    }
    return identifiersBeforeChangedTimestamp;
  }

  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getFilteredIdentifiers(
      TimestampSavePoint<LocalDateTime> timestampSavePoint) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> filteredIdentifiers =
        new ArrayList<>(identifiers);
    CollectionUtils.filter(
        filteredIdentifiers,
        id -> {
          return id.getSavePoint().getTimestamp() != null
              && id.getSavePoint().getTimestamp().isAfter(timestampSavePoint.getTimestamp());
        });
    return filteredIdentifiers;
  }

  protected static List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getNextPage(
      PageRequest pageRequest,
      List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> filteredIdentifiers) {
    if (filteredIdentifiers.isEmpty()) {
      return Collections.emptyList();
    }
    int indexFrom = pageRequest.getOffset();
    int indexTo =
        pageRequest.getOffset() + pageRequest.getLimit() > filteredIdentifiers.size()
            ? filteredIdentifiers.size()
            : pageRequest.getOffset() + pageRequest.getLimit();
    return indexFrom < indexTo
        ? new ArrayList<>(filteredIdentifiers.subList(indexFrom, indexTo))
        : Collections.emptyList();
  }
}
