package fr.placide.k8saepccleanarchibsmsaddress.infra.config;

import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.KafkaProducerAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.OutputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.domain.usecase.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UseCaseConfig {
    @Bean
    UseCase configInputAddressService(
            @Autowired KafkaProducerAddressService kafkaProducer,
            @Autowired OutputAddressService outAddressService){
        return new UseCase(kafkaProducer, outAddressService);
    }
}
