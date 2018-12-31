package gov.ca.cwds.jobs.audit.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Created by Alexander Serbin on 12/30/2018
 */
public class CustomEventTest {

  private ObjectMapper objectMapper = Jackson.newObjectMapper();

  @Test
  public void createCustomEvent1() throws Exception {
    AuditEvent auditEvent = new AuditEvent();
    auditEvent.setUserLogin("user1");
    auditEvent.setComment("some comment");
    auditEvent.setEventSource("test");
    auditEvent.setEventType("createdEvent");
    auditEvent.setReservedSort1("sort1");
    auditEvent.setReservedSort2("sort2");
    auditEvent.setReservedSort3("sort3");
    auditEvent.setTimestamp(LocalDateTime.of(2016, 1, 21, 5, 14));
    CreatedEvent createdEvent = new CreatedEvent();
    createdEvent.setCountyType(1);
    createdEvent.setRole("role1");
    auditEvent.setEvent(createdEvent);
    String json = objectMapper.writeValueAsString(auditEvent);
    JSONAssert
        .assertEquals(json, IOUtils.resourceToString("/event1.json", Charset.defaultCharset()),
            false);
  }

  @Test
  public void createCustomEvent2() throws Exception {
    AuditEvent auditEvent = new AuditEvent();
    auditEvent.setUserLogin("user2");
    auditEvent.setComment("some comment2");
    auditEvent.setEventSource("test");
    auditEvent.setEventType("NewValueSetEvent");
    auditEvent.setReservedSort1("sort2");
    auditEvent.setReservedSort2("sort1");
    auditEvent.setReservedSort3("sort3");
    auditEvent.setTimestamp(LocalDateTime.of(2016, 2, 25, 11, 25));
    NewValueSetEvent newValueSetEvent = new NewValueSetEvent();
    newValueSetEvent.setCountyType(2);
    newValueSetEvent.setRole("role2");
    newValueSetEvent.setOldValue("15");
    newValueSetEvent.setNewValue("25");
    auditEvent.setEvent(newValueSetEvent);
    String json = objectMapper.writeValueAsString(auditEvent);
    JSONAssert
        .assertEquals(json, IOUtils.resourceToString("/event2.json", Charset.defaultCharset()),
            false);
  }

  @Test
  public void createCustomEvent3() throws Exception {
    AuditEvent auditEvent = new AuditEvent();
    auditEvent.setUserLogin("user3");
    auditEvent.setComment("some comment3");
    auditEvent.setEventSource("test");
    auditEvent.setEventType("createdEvent");
    auditEvent.setReservedSort1("sort3");
    auditEvent.setReservedSort2("sort2");
    auditEvent.setReservedSort3("sort1");
    auditEvent.setTimestamp(LocalDateTime.of(2017, 7, 2, 1, 13));
    CreatedEvent createdEvent = new CreatedEvent();
    createdEvent.setCountyType(1);
    createdEvent.setRole("role1");
    auditEvent.setEvent(createdEvent);
    String json = objectMapper.writeValueAsString(auditEvent);
    JSONAssert
        .assertEquals(json, IOUtils.resourceToString("/event3.json", Charset.defaultCharset()),
            false);
  }

}
