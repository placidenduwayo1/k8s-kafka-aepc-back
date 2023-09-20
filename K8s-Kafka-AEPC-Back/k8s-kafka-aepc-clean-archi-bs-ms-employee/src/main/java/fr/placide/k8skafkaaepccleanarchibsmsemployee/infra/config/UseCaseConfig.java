package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.config;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.RemoteOutputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.usecase.UseCase;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.service.OutputEmployeeServiceKafkaProducerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UseCaseConfig {
    @Bean
    public UseCase configUseCase(
            @Autowired OutputEmployeeServiceKafkaProducerImpl kafkaProducer,
            @Autowired OutputEmployeeService employeeService,
            @Autowired RemoteOutputAddressService addressService) {
        return new UseCase(kafkaProducer, employeeService, addressService);

    }
}
