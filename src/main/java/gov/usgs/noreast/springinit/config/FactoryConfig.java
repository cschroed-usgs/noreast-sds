package gov.usgs.noreast.springinit.config;

import gov.usgs.noreast.noreastsds.factories.ObservedPropertyFactory;
import gov.usgs.noreast.noreastsds.factories.OfferingFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfig {
	
	@Bean
	public OfferingFactory offeringFactory() {
		return OfferingFactory.getInstance();
	}
	
	@Bean
	public ObservedPropertyFactory observedPropertyFactory() {
		return ObservedPropertyFactory.getInstance();
	}
}
