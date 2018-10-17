package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.cws.dao.CwsChangedIdentifierDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class CwsChangedEntitiesIdentifiersService
    implements ChangedEntitiesIdentifiersService<LocalDateTime> {

  @Inject private CwsChangedIdentifierDao dao;

  @Override
  @UnitOfWork(CMS)
  public Optional<TimestampSavePoint<LocalDateTime>> getNextSavePoint(
      TimestampSavePoint<LocalDateTime> previousSavePoint) {
    return dao.getNextSavePoint(previousSavePoint.getTimestamp()).map(
        LocalDateTimeSavePoint::new);
  }


  @Override
  @UnitOfWork(CMS)
  public Optional<TimestampSavePoint<LocalDateTime>> getFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint) {
    return dao.getFirstChangedTimestampAfterSavepoint(savePoint.getTimestamp())
        .map(LocalDateTimeSavePoint::new);
  }

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      Optional<TimestampSavePoint<LocalDateTime>> previousTimestamp,
      Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp) {
    if (previousTimestamp.isPresent()) {
      if (nextTimestamp.isPresent()) {
        return dao.getIdentifiers(previousTimestamp.get().getTimestamp(),
            nextTimestamp.get().getTimestamp());
      } else {
        return dao.getIdentifiers(previousTimestamp.get().getTimestamp());
      }
    } else {
      return Collections.emptyList();
    }
  }

}