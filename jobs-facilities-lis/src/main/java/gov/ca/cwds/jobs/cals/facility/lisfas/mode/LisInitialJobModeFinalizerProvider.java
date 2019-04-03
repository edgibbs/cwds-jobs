package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Created by Alexander Serbin on 4/1/2019
 */
public class LisInitialJobModeFinalizerProvider implements Provider<LisInitialJobModeFinalizer> {

  @Inject
  private LisInitialJobModeFinalizer lisInitialJobModeFinalizer;

  @Override
  public LisInitialJobModeFinalizer get() {
    return lisInitialJobModeFinalizer;
  }
}
