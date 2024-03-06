package com.ikn.ums.meeting;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
public class UMSMeetingApplication {

	public static void main(String[] args) {
		log.info("UMSMeetingApplication.main() entered");
		SpringApplication.run(UMSMeetingApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate createLoadBalancedRestTemplate() {
		log.info("UMSMeetingApplication.createLoadBalancedRestTemplate() entered");
		RestTemplate loadBalancedRestTemplate = new RestTemplate();
		log.info("UMSMeetingApplication.createLoadBalancedRestTemplate() : RestTemplate object created.");
		return loadBalancedRestTemplate;
	}

	@Bean
	public ModelMapper createMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		return mapper;
	}
}
