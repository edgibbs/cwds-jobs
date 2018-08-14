package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.cws.dao.CwsChangedIdentifierDao;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by Alexander Serbin on 3/6/2018. */
public class CwsIncrementalLoadChangedEntitiesIdentifiersService
    implements ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CwsIncrementalLoadChangedEntitiesIdentifiersService.class);

  @Inject private CwsChangedIdentifierDao recordChangeCwsCmsDao;

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      TimestampSavePoint<LocalDateTime> savePoint, PageRequest pageRequest) {
    LOGGER.info(
        "CwsIncrementalLoadChangedEntitiesIdentifiersService:getIdentifiers, timeStamp: {}",
        savePoint.getTimestamp());
    return recordChangeCwsCmsDao.getIncrementalLoadStream(savePoint.getTimestamp(), pageRequest);
  }

  @Override
  @UnitOfWork(CMS)
  public TimestampSavePoint<LocalDateTime> getFirstChangedTimestamp(
      TimestampSavePoint<LocalDateTime> savePoint) {
    LOGGER.info(
        "CwsIncrementalLoadChangedEntitiesIdentifiersService:getFirstChangedTimestamp, timeStamp: {}",
        savePoint.getTimestamp());
    return new LocalDateTimeSavePoint(
        recordChangeCwsCmsDao.getFirstChangedTimestampForIncrementalLoad(savePoint.getTimestamp()));
  }

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
      getIdentifiersBeforeChangedTimestamp(
          TimestampSavePoint<LocalDateTime> timestampSavePoint, int offset) {
    LOGGER.info(
        "CwsIncrementalLoadChangedEntitiesIdentifiersService:getIdentifiersBeforeChangedTimestamp, timeStamp: {}",
        timestampSavePoint.getTimestamp());
    return recordChangeCwsCmsDao.getIdentifiersBeforeTimestampForIncrementalLoad(
        timestampSavePoint.getTimestamp(), offset);
  }
}
