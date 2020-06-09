package gov.ca.cwds.jobs.common.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by Alexander Serbin on 2/14/2018.
 */
public class LastRunDirHelper {

  private String folder;

  public LastRunDirHelper(String folder) {
    this.folder = folder;
  }

  public void createSavePointContainerFolder() throws IOException {
    FileUtils.forceMkdir(getSavepointContainerFolder().toFile());
  }

  public void deleteSavePointContainerFolder() throws IOException {
    FileUtils.deleteDirectory(getSavepointContainerFolder().toFile());
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") // Path cannot be controlled by the user
  public Path getSavepointContainerFolder() {
    return Paths.get(String.valueOf(folder)).normalize().toAbsolutePath();
  }

}
