package nl.gogognome;

import nl.gogognome.jobscheduler.jobingester.database.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Properties.class)
public class JobIngesterConfiguration {
}
