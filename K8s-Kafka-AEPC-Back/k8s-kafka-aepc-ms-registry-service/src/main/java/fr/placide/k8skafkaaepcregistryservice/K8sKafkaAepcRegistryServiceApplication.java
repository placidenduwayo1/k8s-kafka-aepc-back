package fr.placide.k8skafkaaepcregistryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class K8sKafkaAepcRegistryServiceApplication {

	public static void main(String [] args) {
		new SpringApplication(K8sKafkaAepcRegistryServiceApplication.class)
				.run(args);
	}

}
