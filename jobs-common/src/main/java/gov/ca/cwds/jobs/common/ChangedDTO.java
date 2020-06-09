package gov.ca.cwds.jobs.common;

import gov.ca.cwds.Identifiable;

/**
 * @author CWDS TPT-2
 */
public interface ChangedDTO<E> extends Identifiable<String> {

  RecordChangeOperation getRecordChangeOperation();

  E getDTO();

}
