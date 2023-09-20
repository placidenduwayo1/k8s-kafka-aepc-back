package fr.placide.k8skafkaaepcmsconfigservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class K8sKafkaAepcMsConfigServiceApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertNotNull(this.getClass().getSimpleName());
    }

}
