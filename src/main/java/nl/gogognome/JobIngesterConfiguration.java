package nl.gogognome;

import nl.gogognome.jobscheduler.jobingester.database.JobIngesterProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JobIngesterProperties.class)
public class JobIngesterConfiguration {
}
