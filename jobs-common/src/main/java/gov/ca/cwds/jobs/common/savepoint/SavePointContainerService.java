package gov.ca.cwds.jobs.common.savepoint;

import java.nio.file.Path;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface SavePointContainerService<S extends SavePoint> {

  Path getSavePointFile();

  boolean savePointContainerExists();

  @SuppressWarnings("squid:S1452") // is it not possible to avoid wildcard type in the return parameter due to current design without enormous refactoring efforts
  SavePointContainer<? extends S> readSavePointContainer(
      Class<? extends SavePointContainer<? extends S>> savePointContainerClass);

  void writeSavePointContainer(SavePointContainer<? extends S> savePointContainer);

}
