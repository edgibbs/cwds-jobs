package gov.ca.cwds.jobs.common.savepoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.rest.api.ApiException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public abstract class SavePointContainerServiceImpl<S extends SavePoint> implements
    SavePointContainerService<S> {

  private static final Logger LOG = LoggerFactory.getLogger(SavePointContainerServiceImpl.class);

  private static final String TIMESTAMP_FILENAME = "LastJobRun.time";

  private String outputDir;
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.registerModule(new JavaTimeModule());
  }

  @Inject
  public SavePointContainerServiceImpl(@LastRunDir String outputDir) {
    this.outputDir = outputDir;
  }

  @Override
  public boolean savePointContainerExists() {
    return getSavePointFile().toFile().exists();
  }

  @Override
  public SavePointContainer<? extends S> readSavePointContainer(
      Class<? extends SavePointContainer<? extends S>> savePointContainerClass) {
    try (Reader reader = Files.newBufferedReader(getSavePointFile())) {
      return mapper.readValue(IOUtils.toString(reader), savePointContainerClass);
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new ApiException("Can't get save point container", e);
    }
  }

  @SuppressFBWarnings("PATH_TRAVERSAL_IN") //Path cannot be controlled by the user
  @Override
  public Path getSavePointFile() {
    return Paths.get(outputDir, TIMESTAMP_FILENAME).normalize().toAbsolutePath();
  }

  @Override
  public void writeSavePointContainer(SavePointContainer<? extends S> savePointContainer) {
    Objects.requireNonNull(savePointContainer.getJobMode());
    Objects.requireNonNull(savePointContainer.getSavePoint());
    try {
      String fileContent = mapper.writeValueAsString(savePointContainer);
      File file = getSavePointFile().toFile();
      if (savePointContainerExists()) {
        FileUtils.forceDelete(file);
      }
      if (file.createNewFile()) {
        FileUtils.writeStringToFile(file, fileContent, "UTF-8");
        LOG.info("Save point container {}", fileContent);
        LOG.info("Save point container has been saved");
      } else {
        throw new ApiException(
            "Can't create the file " + getSavePointFile().normalize().toString());
      }
    } catch (IOException e) {
      throw new ApiException("Can't write save point container ", e);
    }
  }
}
