package gov.ca.cwds.jobs.cap.users.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.junit.Test;

public class ChangedUserDtoTest {

  @Test
  public void testEquals() {
    ChangedUserDto changedUserDto = createChangedUserDto("user1@test.ca.gov.com");
    ChangedUserDto otherChangedUserDto = createChangedUserDto("user2@test.ca.gov.com");

    assertEquals(changedUserDto, changedUserDto);
    assertNotEquals(changedUserDto, otherChangedUserDto);
    assertNotEquals(otherChangedUserDto, changedUserDto);
    assertNotEquals(null, changedUserDto);
    assertNotEquals(changedUserDto, null);
  }

  @Test
  public void testHashCode() {
    ChangedUserDto changedUserDto = createChangedUserDto("test@email.com");
    ChangedUserDto otherChangedUserDto = createChangedUserDto("other_test@email.com");
    assertTrue(changedUserDto.hashCode() == changedUserDto.hashCode());
    assertFalse(changedUserDto.hashCode() == otherChangedUserDto.hashCode());
  }

  private ChangedUserDto createChangedUserDto(String email) {
    User user = new User();
    user.setEmail(email);
    return new ChangedUserDto(user, RecordChangeOperation.I);
  }
}
