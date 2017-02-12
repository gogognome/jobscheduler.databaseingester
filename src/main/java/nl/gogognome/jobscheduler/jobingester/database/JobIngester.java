package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobIngester {

    private final JobScheduler jobScheduler;
    private final JobCommandDAO jobCommandDAO;

    public JobIngester(JobScheduler jobScheduler, JobCommandDAO jobCommandDAO) {
        this.jobScheduler = jobScheduler;
        this.jobCommandDAO = jobCommandDAO;
    }

    public void ingestJobs() {
        jobScheduler.runBatch(() -> {
            NewTransaction.runs(() -> {
                List<JobCommand> jobCommands = jobCommandDAO.findJobCommands();
                jobCommands.stream().forEach(j -> {
                    switch (j.getCommand()) {
                        case CREATE: jobScheduler.addJob(j.getJob()); break;
                        case UPDATE: jobScheduler.updateJob(j.getJob()); break;
                        case DELETE: jobScheduler.removeJob(j.getJob().getId()); break;
                    }
                });
                jobCommandDAO.deleteJobCommands(jobCommands);
            });
        });
    }

}
