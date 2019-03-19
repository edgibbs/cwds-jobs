package gov.ca.cwds.jobs.common.entity;

import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import java.time.LocalDateTime;

/**
 * This service uses target API to load target entity by identifier.
 *
 * Created by Alexander Serbin on 2/5/2018.
 */
@FunctionalInterface
public interface ChangedEntityService<E> {

  /**
   * Loads entity from the source. Usually target API services are used to retrieve entity
   * by identifier.
   *
   * @return loaded target entity
   */
  E loadEntity(ChangedEntityIdentifier identifier);

  default void markAllBeforeTimeStampAsProcessed(LocalDateTime timeStamp) {
    throw new UnsupportedOperationException();
  }

}
