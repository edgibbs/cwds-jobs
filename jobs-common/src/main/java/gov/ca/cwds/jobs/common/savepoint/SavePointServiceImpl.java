package gov.ca.cwds.jobs.common.savepoint;

import java.util.List;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class SavePointServiceImpl<S extends SavePoint> implements SavePointService<S> {

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<S> savePointContainerService;

  @Override
  public S loadSavePoint() {
    return savePointContainerService.readSavePointContainer(getSavePointContainerClass())
        .getSavePoint();
  }

  @Override
  public S defineSavepoint(JobBatch<S> jobBatch) {
    List<ChangedEntityIdentifier<S>> changedEntityIdentifiers =
        jobBatch.getChangedEntityIdentifiers();
    return changedEntityIdentifiers.get(changedEntityIdentifiers.size() - 1).getSavePoint();
  }

}
