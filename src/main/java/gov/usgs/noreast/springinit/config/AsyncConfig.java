package gov.usgs.noreast.springinit.config;

import gov.usgs.noreast.noreastsds.services.async.SOSDataQuery;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
	@Bean
	public SOSDataQuery sosDataQuery() {
		return new SOSDataQuery();
	}
}
