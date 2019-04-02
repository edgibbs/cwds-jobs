package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.util.List;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class SavePointServiceImpl<S extends SavePoint> implements
    SavePointService<S> {

  @Inject
  private SavePointContainerService<S> savePointContainerService;

  @Override
  public S loadSavePoint() {
    return savePointContainerService.readSavePointContainer(getSavePointContainerClass())
        .getSavePoint();
  }

  @Override
  public S defineSavepoint(JobBatch<S> jobBatch) {
    List<ChangedEntityIdentifier<S>> changedEntityIdentifiers = jobBatch
        .getChangedEntityIdentifiers();
    return changedEntityIdentifiers
        .get(changedEntityIdentifiers.size() - 1).getSavePoint();
  }

}
