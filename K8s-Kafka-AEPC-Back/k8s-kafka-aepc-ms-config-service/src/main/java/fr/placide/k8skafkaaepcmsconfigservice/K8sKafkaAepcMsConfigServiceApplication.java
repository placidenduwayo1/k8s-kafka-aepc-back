package fr.placide.k8skafkaaepcmsconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class K8sKafkaAepcMsConfigServiceApplication {

    public static void main(String[] args) {
       new SpringApplication(K8sKafkaAepcMsConfigServiceApplication.class)
               .run(args);
    }

}
