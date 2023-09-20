package fr.placide.k8skafkaaepccleanarchibsmsproject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class K8sKafkaAepcCleanArchiBsMsProjectApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertNotNull(this.getClass().getSimpleName());
	}

}
