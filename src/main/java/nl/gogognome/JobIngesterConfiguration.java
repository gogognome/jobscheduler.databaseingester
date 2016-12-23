package nl.gogognome;

import nl.gogognome.jobscheduler.jobingester.database.Properties;
import nl.gogognome.jobscheduler.scheduler.JobFinder;
import nl.gogognome.jobscheduler.scheduler.JobPersister;
import nl.gogognome.jobscheduler.scheduler.JobScheduler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Properties.class)
public class JobIngesterConfiguration {

    @Bean
    public JobScheduler jobScheduler(JobFinder jobFinder, JobPersister jobPersister) {
        return new JobScheduler(jobFinder, jobPersister);
    }

    @Bean
    public JobFinder jobFinder() {
        return null;
    }

    @Bean
    public JobPersister jobPersister() {
        return null;
    }
}
