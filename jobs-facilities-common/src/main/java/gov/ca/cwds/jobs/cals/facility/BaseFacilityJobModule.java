package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import gov.ca.cwds.cals.inject.MappingModule;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchAliasFinalizerProvider;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public abstract class BaseFacilityJobModule<T extends BaseFacilityJobConfiguration> extends AbstractModule {

  private final JobMode jobMode;
  private Class<? extends BulkWriter<ChangedFacilityDto>> facilityElasticWriterClass;
  private Class<? extends Provider<? extends JobModeFinalizer>> primaryJobFinalizerProviderClass;

  private T jobConfiguration;

  public BaseFacilityJobModule(T jobConfiguration, JobMode jobMode) {
    this.facilityElasticWriterClass = FacilityElasticWriter.class;
    this.jobConfiguration = jobConfiguration;
    this.primaryJobFinalizerProviderClass = ElasticsearchAliasFinalizerProvider.class;
    this.jobMode = jobMode;
  }

  public void setFacilityElasticWriterClass(
      Class<? extends BulkWriter<ChangedFacilityDto>> facilityElasticWriterClass) {
    this.facilityElasticWriterClass = facilityElasticWriterClass;
  }

  public JobMode getJobMode() {
    return jobMode;
  }

  public T getJobConfiguration() {
    return jobConfiguration;
  }

  public Class<? extends Provider<? extends JobModeFinalizer>> getPrimaryJobFinalizerProviderClass() {
    return primaryJobFinalizerProviderClass;
  }

  public void setPrimaryJobFinalizerProviderClass(
      Class<? extends Provider<? extends JobModeFinalizer>> primaryJobFinalizerProviderClass) {
    this.primaryJobFinalizerProviderClass = primaryJobFinalizerProviderClass;
  }

  @Override
  protected void configure() {
    bind(new TypeLiteral<BulkWriter<ChangedFacilityDto>>() {
    }).to(facilityElasticWriterClass);
    install(new MappingModule());
    install(new NsDataAccessModule(jobConfiguration.getCalsnsDataSourceFactory()));
  }

}
