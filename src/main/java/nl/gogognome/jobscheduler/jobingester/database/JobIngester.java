package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.dataaccess.transaction.NewTransaction;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JobIngester {

    private final JobScheduler jobScheduler;
    private final JobCommandDAO jobCommandDAO;

    public JobIngester(JobScheduler jobScheduler, JobCommandDAO jobCommandDAO) {
        this.jobScheduler = jobScheduler;
        this.jobCommandDAO = jobCommandDAO;
    }

    /**
     * Reads job commands from the database and forwards them to the job scheduler.
     * @return the number of job commands handled
     */
    public int ingestJobs() {
        AtomicInteger nrJobCommandsHandled = new AtomicInteger();
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
                nrJobCommandsHandled.set(jobCommands.size());
            });
        });
        return nrJobCommandsHandled.intValue();
    }

}
