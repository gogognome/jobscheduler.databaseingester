package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class JobIngesterTest {

    private JobScheduler jobScheduler = mock(JobScheduler.class);
    private JobIngestDAO jobIngestDAO = mock(JobIngestDAO.class);
    private JobIngester jobIngester = new JobIngester(jobScheduler, jobIngestDAO);

    private List<JobCommand> jobCommandsInDatabase = new ArrayList<>();

    @Before
    public void initMocks() throws SQLException {
        when(jobIngestDAO.findAll()).thenReturn(jobCommandsInDatabase);
    }

    @Test
    public void ingestJobs_zeroJobCommands_addsNoJobsAndDeletesNoJobCommandsFromDatabase() throws SQLException {
        jobCommandsInDatabase.clear();

        jobIngester.ingestJobs();

        verify(jobScheduler, never()).addJob(any(Job.class));
        verify(jobIngestDAO, times(1)).delete(jobCommandsInDatabase);
    }

    @Test
    public void ingestJobs_oneJobCommand_addsOneJobAndsDeletesJobCommandFromDatabase() throws SQLException {
        Job job1 = new Job("1");
        jobCommandsInDatabase.add(new JobCommand(Command.CREATE, job1));

        jobIngester.ingestJobs();

        verify(jobScheduler, times(1)).addJob(eq(job1));
        verify(jobIngestDAO, times(1)).delete(jobCommandsInDatabase);
    }

    @Test
    public void ingestJobs_threeJobCommandsWithDifferentCommands_eachOfTheCommandsAreHandledAndJobCommandsArDeletedFromDatabase() throws SQLException {
        Job job1 = new Job("1");
        jobCommandsInDatabase.add(new JobCommand(Command.CREATE, job1));
        Job job2 = new Job("2");
        jobCommandsInDatabase.add(new JobCommand(Command.UPDATE, job2));
        Job job3 = new Job("3");
        jobCommandsInDatabase.add(new JobCommand(Command.DELETE, job3));

        jobIngester.ingestJobs();

        verify(jobScheduler, times(1)).addJob(eq(job1));
        verify(jobScheduler, times(1)).updateJob(eq(job2));
        verify(jobScheduler, times(1)).removeJob(eq(job3.getId()));
        verify(jobIngestDAO, times(1)).delete(jobCommandsInDatabase);
    }
}
