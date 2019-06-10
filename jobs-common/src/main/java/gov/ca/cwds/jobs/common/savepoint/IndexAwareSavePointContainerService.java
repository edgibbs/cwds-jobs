package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.BaseContainerService;
import gov.ca.cwds.jobs.common.inject.IndexName;
import java.nio.file.Path;

/**
 * Created by Alexander Serbin on 6/5/2019
 */
public class IndexAwareSavePointContainerService<S extends SavePoint> implements
    SavePointContainerService<S> {

  @Inject
  @BaseContainerService
  private SavePointContainerService<S> savePointContainerService;

  @Inject
  @IndexName
  private String indexName;

  @Override
  public Path getSavePointFile() {
    return savePointContainerService.getSavePointFile();
  }

  @Override
  public boolean savePointContainerExists() {
    return savePointContainerService.savePointContainerExists();
  }

  @Override
  public SavePointContainer<? extends S> readSavePointContainer(
      Class<? extends SavePointContainer<? extends S>> savePointContainerClass) {
    return savePointContainerService.readSavePointContainer(savePointContainerClass);
  }

  @Override
  public void writeSavePointContainer(SavePointContainer<? extends S> savePointContainer) {
    savePointContainer.setIndexName(indexName);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }

  public void setSavePointContainerService(
      SavePointContainerService<S> savePointContainerService) {
    this.savePointContainerService = savePointContainerService;
  }

  public void setIndexName(String indexName) {
    this.indexName = indexName;
  }

}


