package gov.ca.cwds.jobs.common;

import java.util.List;

/**
 * @param <E> type to write
 * @author CWDS TPT-2
 */
@FunctionalInterface
public interface BulkWriter<E> {

  void write(List<E> items);

  default void destroy() {
    //default method
  }

  default void flush() {
    //default method
  }

}
