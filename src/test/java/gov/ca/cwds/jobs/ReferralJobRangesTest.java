package gov.ca.cwds.jobs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class ReferralJobRangesTest {

  @Test
  public void type() throws Exception {
    assertThat(ReferralJobRanges.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    ReferralJobRanges target = new ReferralJobRanges();
    assertThat(target, notNullValue());
  }

  private void checkPartitionRanges(String schema, boolean isZOS, int expectedCnt)
      throws Exception {
    ReferralJobRanges target = new ReferralJobRanges();
    System.setProperty("DB_CMS_SCHEMA", schema);
    BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job =
        mock(BasePersonIndexerJob.class);
    when(job.isDB2OnZOS()).thenReturn(isZOS);
    final List<Pair<String, String>> actual = target.getPartitionRanges(job);
    final int cntActual = actual.size();
    assertThat(cntActual, is(equalTo(expectedCnt)));

    // TODO: Check key order and range for ASCII and EBCDIC.
    final Pair<String, String> p = actual.get(0);
  }

  @Test
  public void getPartitionRanges_RSQ() throws Exception {
    checkPartitionRanges("CWSRSQ", true, 3562);
  }

  @Test
  public void getPartitionRanges_REP() throws Exception {
    checkPartitionRanges("CWSREP", true, 3562);
  }

  @Test
  public void getPartitionRanges_RS1() throws Exception {
    checkPartitionRanges("CWSRS1", true, 1);
  }

  @Test
  public void getPartitionRanges_RS1_Linux() throws Exception {
    checkPartitionRanges("CWSRS1", false, 1);
  }

}