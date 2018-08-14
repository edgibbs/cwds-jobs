package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.cws.dao.CwsChangedIdentifierDao;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by Alexander Serbin on 3/6/2018. */
public class CwsInitialResumeLoadChangedEntitiesIdentifiersService
    extends CwsInitialLoadChangedEntitiesIdentifiersService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CwsInitialResumeLoadChangedEntitiesIdentifiersService.class);

  @Inject private CwsChangedIdentifierDao recordChangeCwsCmsDao;

  @Override
  @UnitOfWork(CMS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      TimestampSavePoint<LocalDateTime> timeStampAfter, PageRequest pageRequest) {
    LOGGER.info("CwsInitialResumeLoadChangedEntitiesIdentifiersService:getIdentifiers, timeStampAfter: {}", timeStampAfter);
    return recordChangeCwsCmsDao.getResumeInitialLoadStream(
        timeStampAfter.getTimestamp(), pageRequest);
  }
}
