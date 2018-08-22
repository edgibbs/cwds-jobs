package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public class TimestampInitialModeImplementor<E> extends
    AbstractTimestampJobModeImplementor<E, LocalDateTime, DefaultJobMode> {

  @Inject
  private LocalDateTimeJobModeFinalizer jobModeFinalizer;

  @Inject
  private ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> changedEntitiesIdentifiersService;

  @Override
  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getNextPage(
      PageRequest pageRequest) {
    return changedEntitiesIdentifiersService.getIdentifiersForInitialLoad(pageRequest);
  }

  public void setChangedEntitiesIdentifiersService(
      ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> changedEntitiesIdentifiersService) {
    this.changedEntitiesIdentifiersService = changedEntitiesIdentifiersService;
  }

  @Override
  public void doFinalizeJob() {
    jobModeFinalizer.doFinalizeJob();
  }

}
