package fr.placide.k8skafkaaepcgatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class K8sKafkaAepcGatewayServiceApplication {
	public static void main(String [] args){
		new SpringApplication(K8sKafkaAepcGatewayServiceApplication.class)
				.run(args);
	}
	@Bean
	DiscoveryClientRouteDefinitionLocator routeDiscover(
			ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp){
		return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
	}
}
