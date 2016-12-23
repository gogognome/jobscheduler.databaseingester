package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobIngester {

    private final JobScheduler jobScheduler;
    private final JobIngestDAO jobIngestDAO;
    private final Properties properties;

    private Thread thread;
    private boolean threadRunning;
    private Object lock = new Object();

    public JobIngester(JobScheduler jobScheduler, JobIngestDAO jobIngestDAO, Properties properties) {
        this.jobScheduler = jobScheduler;
        this.jobIngestDAO = jobIngestDAO;
        this.properties = properties;
    }

    public void start() {
        synchronized (lock) {
            if (threadRunning) {
                throw new IllegalStateException("The job ingester is still running");
            }
            threadRunning = false;
            thread = new Thread(() -> timerThread());
            thread.start();
        }
    }

    public void stop() {
        synchronized (lock) {
            if (!threadRunning) {
                throw new IllegalStateException("The job ingester is not running");
            }
            threadRunning = false;
            lock.notify();
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore this exception
        }
        thread = null;
    }

    private void ingestJobs() {
        NewTransaction.runs(() -> {
            List<Job> jobs = jobIngestDAO.findAll();
            jobs.stream().forEach(j -> jobScheduler.addJob(j));
            jobIngestDAO.delete(jobs);
        });
    }

    private void timerThread() {
        while (true) {
            synchronized (lock) {
                if (!threadRunning) {
                    return;
                }
            }
            ingestJobs();

            synchronized (lock) {
                try {
                    lock.wait(properties.getDelayBetweenPolls());
                } catch (InterruptedException e) {
                    // ignore this exception
                }
            }
        }
    }
}
