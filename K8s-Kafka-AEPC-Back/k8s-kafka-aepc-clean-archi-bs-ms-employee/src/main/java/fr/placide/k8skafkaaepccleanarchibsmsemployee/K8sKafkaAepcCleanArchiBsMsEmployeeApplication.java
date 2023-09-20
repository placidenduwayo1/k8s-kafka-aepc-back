package fr.placide.k8skafkaaepccleanarchibsmsemployee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class K8sKafkaAepcCleanArchiBsMsEmployeeApplication {

	public static void main(String[] args) {
		new SpringApplication(K8sKafkaAepcCleanArchiBsMsEmployeeApplication.class)
				.run(args);
	}
}
