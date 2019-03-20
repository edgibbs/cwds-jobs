package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.jobs.common.BulkWriter;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public abstract class BaseFacilityJobModule<T extends BaseFacilityJobConfiguration> extends AbstractModule {

  private final String lastRunDir;
  private Class<? extends BulkWriter<ChangedFacilityDto>> facilityElasticWriterClass;
  private T jobConfiguration;

  public BaseFacilityJobModule(T jobConfiguration, String lastRunDir) {
    this.facilityElasticWriterClass = FacilityElasticWriter.class;
    this.jobConfiguration = jobConfiguration;
    this.lastRunDir = lastRunDir;
  }

  public void setFacilityElasticWriterClass(
      Class<? extends BulkWriter<ChangedFacilityDto>> facilityElasticWriterClass) {
    this.facilityElasticWriterClass = facilityElasticWriterClass;
  }

  public T getJobConfiguration() {
    return jobConfiguration;
  }

  public String getLastRunDir() {
    return lastRunDir;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<BulkWriter<ChangedFacilityDto>>() {
    }).to(facilityElasticWriterClass);
    install(new MappingModule());
    install(new CalsNsDataAccessModule(jobConfiguration.getCalsnsDataSourceFactory()));
  }

}
