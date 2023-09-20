package fr.placide.k8skafkaaepcgatewayservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class K8sKafkaAepcGatewayServiceApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertNotNull(this.getClass().getSimpleName());
	}

}
