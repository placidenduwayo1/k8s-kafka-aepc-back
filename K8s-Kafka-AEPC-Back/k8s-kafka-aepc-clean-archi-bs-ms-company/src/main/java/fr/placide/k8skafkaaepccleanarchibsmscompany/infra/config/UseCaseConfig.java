package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.config;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output.OutputCompanyService;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.usecase.UseCase;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.services.KafkaProducerCompanyEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UseCaseConfig {
    @Bean
    UseCase configCompanyUseCase(@Autowired KafkaProducerCompanyEventService kafkaProducer,
                                 @Autowired OutputCompanyService companyService){
        return new UseCase(kafkaProducer, companyService);
    }
}
