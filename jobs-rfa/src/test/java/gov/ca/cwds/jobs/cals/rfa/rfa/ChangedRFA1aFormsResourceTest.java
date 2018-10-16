package gov.ca.cwds.jobs.cals.rfa.rfa;

import org.junit.Ignore;

/**
 * @author CWDS TPT-2
 */

//TODO unignore once RFA1a job is ready
@Ignore
public class ChangedRFA1aFormsResourceTest /*extends BaseApiTest<TestCalsJobsConfiguration>*/ {
/*
  static final String PATH_CHANGED_RFA_1A_FORMS = "changed-" + RFA_1A_FORMS;
  static final String PATH_PARAM_DATE_AFTER = "dateAfter";

  @ClassRule
  public static final BaseDropwizardApplication<TestCalsJobsConfiguration> application
      = new BaseDropwizardApplication<>(TestCalsJobsApplication.class,
      "configuration/test-application.yml");

  @Override
  protected BaseDropwizardApplication<TestCalsJobsConfiguration> getApplication() {
    return application;
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    DatabaseHelper
        .setUpDatabase(application.getConfiguration().getNsDataSourceFactory(), DataSourceName.NS);
  }

  @Test
  public void getChangedRFA1aFormsTest() throws Exception {
    CollectionDTO<ChangedRFA1aFormDTO> rfaForms = getChangedRFA1aFormsAfter("1970-01-01 00:00:00");
    int numberOfChangedAfter19700101 = rfaForms.getCollection().size();
    // there are at least 2 RFA1a Forms that were created or modified after 1970-01-01 00:00:00
    assertTrue(numberOfChangedAfter19700101 >= 2);
    ChangedRFA1aFormDTO changedRFA1aFormDTO = rfaForms.getCollection().iterator().next();
    assertTrue(RecordChangeOperation.I == changedRFA1aFormDTO.getRecordChangeOperation());

    String after = "2017-07-18 10:01:00";
    rfaForms = getChangedRFA1aFormsAfter(after);
    int numberOfChangedAfter20170718 = rfaForms.getCollection().size();
    // there is one RFA1a Form that was created or modified before 2017-07-18 10:01:00,
    // and this is why this assertion should pass:
    assertEquals(numberOfChangedAfter20170718, numberOfChangedAfter19700101 - 1);
    // the form that was created or modified before 2017-07-18 10:01:00 has id = 1,
    // so it should not be found in the collection:
    Optional<RFA1aFormDTO> optional = rfaForms.getCollection().stream()
        .map(ChangedRFA1aFormDTO::getDTO)
        .filter(rfa1aForm -> rfa1aForm.getId() == 1).findFirst();
    assertFalse(optional.isPresent());

    rfaForms = getChangedRFA1aFormsAfter("2222-01-01 00:00:00");
    assertTrue(rfaForms.getCollection().size() == 0);
  }

  private CollectionDTO<ChangedRFA1aFormDTO> getChangedRFA1aFormsAfter(String dateTime)
      throws IOException {
    WebTarget target = clientTestRule.target(PATH_CHANGED_RFA_1A_FORMS + "/" + dateTime);
    Invocation.Builder invocation = target.request(MediaType.APPLICATION_JSON);
    return Jackson.newObjectMapper().readValue(invocation.get(String.class),
        new TypeReference<CollectionDTO<ChangedRFA1aFormDTO>>() {
        });
  }
*/
}
