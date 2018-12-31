package gov.ca.cwds.jobs.audit.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Created by Alexander Serbin on 12/30/2018
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreatedEvent extends CustomEvent {

}
