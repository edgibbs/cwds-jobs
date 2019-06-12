package gov.ca.cwds.jobs.common.core;

import gov.ca.cwds.jobs.common.inject.JobModule;

/**
 * Created by Alexander Serbin on 11/20/2018
 */
public interface JobModuleBuilder {

  JobModule buildJobModule(String[] args, boolean elasticSearchModule);

}
