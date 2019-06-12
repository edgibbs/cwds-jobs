package gov.ca.cwds.jobs.common;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.core.JobImpl;
import gov.ca.cwds.jobs.common.entity.TestEntity;
import gov.ca.cwds.jobs.common.inject.TestSessionFactory;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 10/13/2018
 */
public class TestJobImpl extends
    JobImpl<TestEntity, TimestampSavePoint<LocalDateTime>> {

    @Inject
    @TestSessionFactory
    private SessionFactory testSessionFactory;

    @Override
    public void close() {
      super.close();
      testSessionFactory.close();
    }

}
