package nl.gogognome.jobscheduler.jobingester.database;

import nl.gogognome.jobscheduler.scheduler.Job;
import nl.gogognome.jobscheduler.scheduler.JobState;

import java.time.Instant;

public class JobCommandBuilder {

    public static JobCommand buildJob(String id, Command command) {
        Job job = new Job(id);
        job.setCreationInstant(Instant.now());
        job.setType("Test");
        job.setData("Data");
        job.setState(JobState.IDLE);
        return new JobCommand(command, job);
    }

}
