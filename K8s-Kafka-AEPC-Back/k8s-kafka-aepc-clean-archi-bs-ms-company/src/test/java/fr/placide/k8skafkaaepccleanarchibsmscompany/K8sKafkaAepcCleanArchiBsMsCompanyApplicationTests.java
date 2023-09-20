package fr.placide.k8skafkaaepccleanarchibsmscompany;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class K8sKafkaAepcCleanArchiBsMsCompanyApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertNotNull(this.getClass().getSimpleName());
	}

}
