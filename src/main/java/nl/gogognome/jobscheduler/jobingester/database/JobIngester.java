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
            List<JobCommand> jobCommands = jobIngestDAO.findAll();
            jobCommands.stream().forEach(j -> {
                switch (j.getCommand()) {
                    case CREATE: jobScheduler.addJob(j.getJob()); break;
                    case UPDATE: jobScheduler.updateJob(j.getJob()); break;
                    case DELETE: jobScheduler.removeJob(j.getJob().getId()); break;
                }
            });
            jobIngestDAO.delete(jobCommands);
        });
    }

}
