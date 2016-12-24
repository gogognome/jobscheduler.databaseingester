package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

public class JobIngesterTest {

    private JobScheduler jobScheduler = mock(JobScheduler.class);
    private JobIngestDAO jobIngestDAO = mock(JobIngestDAO.class);
    private JobIngester jobIngester = new JobIngester(jobScheduler, jobIngestDAO);

    private List<Job> jobsInDatabase = new ArrayList<>();

    @Before
    public void initMocks() throws SQLException {
        when(jobIngestDAO.findAll()).thenReturn(jobsInDatabase);
    }

    @Test
    public void ingestJobs_zeroJobs_addsNoJobsAndDeletesNoJobsFromDatabase() throws SQLException {
        jobsInDatabase.clear();

        jobIngester.ingestJobs();

        verify(jobScheduler, never()).addJob(any(Job.class));
        verify(jobIngestDAO, times(1)).delete(jobsInDatabase);
    }

    @Test
    public void ingestJobs_oneJob_addsOneJobAndsDeletesJobFromDatabase() throws SQLException {
        Job job1 = new Job("1");
        jobsInDatabase.add(job1);

        jobIngester.ingestJobs();

        verify(jobScheduler, times(1)).addJob(eq(job1));
        verify(jobIngestDAO, times(1)).delete(jobsInDatabase);
    }

    @Test
    public void ingestJobs_twoJobs_addsTwoJobsAndDeletesJobsFromDatabase() throws SQLException {
        Job job1 = new Job("1");
        jobsInDatabase.add(job1);
        Job job2 = new Job("2");
        jobsInDatabase.add(job2);

        jobIngester.ingestJobs();

        verify(jobScheduler, times(1)).addJob(eq(job1));
        verify(jobScheduler, times(1)).addJob(eq(job2));
        verify(jobIngestDAO, times(1)).delete(jobsInDatabase);
    }
}
