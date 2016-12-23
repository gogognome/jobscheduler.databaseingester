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

    public JobIngester(JobScheduler jobScheduler, JobIngestDAO jobIngestDAO) {
        this.jobScheduler = jobScheduler;
        this.jobIngestDAO = jobIngestDAO;
    }

    public void ingestJobs() {
        NewTransaction.runs(() -> {
            List<Job> jobs = jobIngestDAO.findAll();
            jobs.stream().forEach(j -> jobScheduler.addJob(j));
            jobIngestDAO.delete(jobs);
        });
    }


}
