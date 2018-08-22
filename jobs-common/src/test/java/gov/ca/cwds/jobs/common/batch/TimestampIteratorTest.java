package gov.ca.cwds.jobs.common.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.TestChangedIdentifiersService;
import gov.ca.cwds.jobs.common.TestEntity;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.TimestampIdentifier;
import gov.ca.cwds.jobs.common.mode.TimestampInitialModeImplementor;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/30/2018.
 */
//TODO unignore when SEAR-319 implemented
@Ignore
public class TimestampIteratorTest {

  private LocalDateTimeSavePointService savePointService = new LocalDateTimeSavePointService();

  @Test
  public void noRecordsTest() {
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(1,
        new TestChangedIdentifiersService(Collections.emptyList()));
    assertTrue(jobIterator.getNextPortion().isEmpty());
  }

  @Test
  public void savePointInSecondPageTest() {
    LocalDateTime timestamp = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    LocalDateTime differentTimestamp = LocalDateTime.of(2017, 6, 4, 1, 10, 22);
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> sameTimestampIdentifier = new TimestampIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new LocalDateTimeSavePoint(timestamp));
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> differentTimestampIdentifier = new TimestampIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new LocalDateTimeSavePoint(differentTimestamp));
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers = new ArrayList<>(
        Arrays.asList(
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            differentTimestampIdentifier
        ));
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(identifiers));
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(5, firstPortion.get(0).getSize());
    assertEquals(timestamp, savePointService.defineSavepoint(firstPortion.get(0)).getTimestamp() /*firstPortion.get(0).getSavePoint()*/);
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(differentTimestamp,
        savePointService.defineSavepoint(secondPortion.get(0)).getTimestamp());
  }

  @Test
  public void emptyTimestampsLessThenOnePage() {
    ChangedEntityIdentifier emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier)));
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(null, savePointService.defineSavepoint(portion.get(0)).getTimestamp());
    assertEquals(new LocalDateTimeSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(0).getSavePoint());
    assertEquals(new LocalDateTimeSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(1).getSavePoint());
  }

  @Test
  public void emptyTimestampsExactOnePageSize() {
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> emptyTimestampIdentifier = createEmptyIdentifier();
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(emptyTimestampIdentifier,
            emptyTimestampIdentifier, emptyTimestampIdentifier)));
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> portion = jobIterator.getNextPortion();
    assertEquals(1, portion.size());
    assertEquals(3, portion.get(0).getSize());
    assertEquals(new LocalDateTimeSavePoint(null),
        savePointService.defineSavepoint(portion.get(0)));
    assertEquals(new LocalDateTimeSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(0).getSavePoint());
    assertEquals(new LocalDateTimeSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(1).getSavePoint());
    assertEquals(new LocalDateTimeSavePoint(null),
        portion.get(0).getChangedEntityIdentifiers().get(2).getSavePoint());
  }

  @Test
  public void emptyTimestampsLessThenOnePageSizeAndSomeNotEmpty() {
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> emptyTimestampIdentifier = createEmptyIdentifier();
    LocalDateTime timestamp1 = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    LocalDateTime timestamp2 = LocalDateTime.of(2014, 6, 1, 2, 10, 13);
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(3,
        new TestChangedIdentifiersService(Arrays.asList(
            emptyTimestampIdentifier,
            emptyTimestampIdentifier,
            new TimestampIdentifier<>("testId2",
                RecordChangeOperation.I,
                new LocalDateTimeSavePoint(timestamp1)),
            new TimestampIdentifier<>("testId3",
                RecordChangeOperation.I,
                new LocalDateTimeSavePoint(timestamp2))
        )));
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> firstPortion = jobIterator.getNextPortion();
    assertEquals(1, firstPortion.size());
    assertEquals(3, firstPortion.get(0).getSize());
    assertEquals(timestamp1, savePointService.defineSavepoint(firstPortion.get(0)).getTimestamp());
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> secondPortion = jobIterator.getNextPortion();
    assertEquals(1, secondPortion.size());
    assertEquals(1, secondPortion.get(0).getSize());
    assertEquals(timestamp2, savePointService.defineSavepoint(secondPortion.get(0)).getTimestamp());
    assertEquals("testId3", secondPortion.get(0).getChangedEntityIdentifiers().get(0).getId());
  }

  @Test
  public void manyPagesOfEqualTimestamps() {
    LocalDateTime timestamp = LocalDateTime.of(2013, 5, 8, 1, 10, 25);
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> sameTimestampIdentifier = new TimestampIdentifier<>(
        "testId",
        RecordChangeOperation.I,
        new LocalDateTimeSavePoint(timestamp));
    JobBatchIterator<TimestampSavePoint<LocalDateTime>> jobIterator = prepareBatchIterator(2,
        new TestChangedIdentifiersService(Arrays.asList(
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier,
            sameTimestampIdentifier
        )));
    List<JobBatch<TimestampSavePoint<LocalDateTime>>> portion = jobIterator.getNextPortion();
    assertEquals(3, portion.size());
    assertEquals(2, portion.get(0).getSize());
    assertEquals(2, portion.get(1).getSize());
    assertEquals(1, portion.get(2).getSize());
    assertEquals(timestamp, savePointService.defineSavepoint(portion.get(2)).getTimestamp());
  }

  private ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> createEmptyIdentifier() {
    return new TimestampIdentifier<>("testId",
        RecordChangeOperation.I,
        new LocalDateTimeSavePoint(null));
  }

  private JobBatchIterator<TimestampSavePoint<LocalDateTime>> prepareBatchIterator(int batchSize,
      ChangedEntitiesIdentifiersService<TimestampSavePoint<LocalDateTime>> identifiersProvider) {
    TimestampInitialModeImplementor<TestEntity> jobBatchIterator = new TimestampInitialModeImplementor<>();
    jobBatchIterator.setBatchSize(batchSize);
    jobBatchIterator.setChangedEntitiesIdentifiersService(identifiersProvider);
    return jobBatchIterator;
  }

}