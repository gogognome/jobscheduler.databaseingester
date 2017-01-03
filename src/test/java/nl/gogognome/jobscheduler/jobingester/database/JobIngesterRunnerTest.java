package nl.gogognome.jobscheduler.jobingester.database;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class JobIngesterRunnerTest {

    private JobIngesterProperties properties = new JobIngesterProperties();
    private JobIngester jobIngester = mock(JobIngester.class);
    private JobIngesterRunner jobIngesterRunner = new JobIngesterRunner(properties, jobIngester);

    @Test
    public void runForHalfPollingInterval_shouldIngestJobsOnce() throws InterruptedException {
        jobIngesterRunner.start();
        try {
            Thread.sleep(properties.getDelayBetweenPolls() / 2);
        } finally {
            jobIngesterRunner.stop();
        }

        verify(jobIngester, times(1)).ingestJobs();
    }

    @Test
    public void runForOneAndAHalfPollingInterval_shouldIngestJobsTwice() throws InterruptedException {
        jobIngesterRunner.start();

        try {
            Thread.sleep(properties.getDelayBetweenPolls() * 3 / 2);
        } finally {
            jobIngesterRunner.stop();
        }

        verify(jobIngester, times(2)).ingestJobs();
    }

    @Test
    public void startTwice_shouldFail() {
        jobIngesterRunner.start();

        try {
            jobIngesterRunner.start();
            fail("Expected exception not thrown");
        } catch (IllegalStateException e) {
            assertEquals("The job ingester is still running", e.getMessage());
        } finally {
            jobIngesterRunner.stop();
        }
    }

    @Test
    public void stoppingTwice_shouldFail() {
        jobIngesterRunner.start();
        jobIngesterRunner.stop();

        try {
            jobIngesterRunner.stop();
            fail("Expected exception not thrown");
        } catch (IllegalStateException e) {
            assertEquals("The job ingester is not running", e.getMessage());
        }
    }
}
