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
    private JobCommandDAO jobCommandDAO = mock(JobCommandDAO.class);
    private JobIngester jobIngester = new JobIngester(jobScheduler, jobCommandDAO);

    private List<JobCommand> jobCommandsInDatabase = new ArrayList<>();

    @Before
    public void initMocks() throws SQLException {
        when(jobCommandDAO.findJobCommands()).thenReturn(jobCommandsInDatabase);
        doAnswer(invocationOnMock -> { ((Runnable)invocationOnMock.getArguments()[0]).run(); return null; })
                .when(jobScheduler).runBatch(any(Runnable.class));
    }

    @Test
    public void ingestJobs_zeroJobCommands_addsNoJobsAndDeletesNoJobCommandsFromDatabase() throws SQLException {
        jobCommandsInDatabase.clear();

        jobIngester.ingestJobs();

        verify(jobScheduler, never()).addJob(any(Job.class));
        verify(jobCommandDAO).deleteJobCommands(jobCommandsInDatabase);
    }

    @Test
    public void ingestJobs_oneJobCommand_addsOneJobAndsDeletesJobCommandFromDatabase() throws SQLException {
        Job job1 = new Job("1");
        jobCommandsInDatabase.add(new JobCommand(Command.CREATE, job1));

        jobIngester.ingestJobs();

        verify(jobScheduler).addJob(eq(job1));
        verify(jobCommandDAO).deleteJobCommands(jobCommandsInDatabase);
    }

    @Test
    public void ingestJobs_threeJobCommandsWithDifferentCommands_eachOfTheCommandsAreHandledAndJobCommandsArDeletedFromDatabase() throws SQLException {
        JobCommand jobCommand1 = JobCommandBuilder.buildJob("1", Command.CREATE);
        jobCommandsInDatabase.add(jobCommand1);
        JobCommand jobCommand2 = JobCommandBuilder.buildJob("1", Command.UPDATE);
        jobCommandsInDatabase.add(jobCommand2);
        JobCommand jobCommand3 = JobCommandBuilder.buildJob("1", Command.DELETE);
        jobCommandsInDatabase.add(jobCommand3);

        jobIngester.ingestJobs();

        verify(jobScheduler).addJob(eq(jobCommand1.getJob()));
        verify(jobScheduler).updateJob(eq(jobCommand2.getJob()));
        verify(jobScheduler).removeJob(eq(jobCommand3.getJob().getId()));
        verify(jobCommandDAO).deleteJobCommands(jobCommandsInDatabase);
    }
}
