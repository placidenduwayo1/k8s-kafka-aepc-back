package fr.placide.k8skafkaaepccleanarchibsmsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class K8sKafkaAepcCleanArchiBsMsProjectApplication {

	public static void main(String[] args) {
		new SpringApplication(K8sKafkaAepcCleanArchiBsMsProjectApplication.class)
				.run(args);
	}

}
