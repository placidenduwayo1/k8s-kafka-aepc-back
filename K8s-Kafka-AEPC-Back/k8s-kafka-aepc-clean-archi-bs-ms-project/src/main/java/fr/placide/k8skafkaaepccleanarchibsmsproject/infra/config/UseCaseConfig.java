package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.config;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.usecase.UseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class UseCaseConfig {
    @Bean
    public UseCase configureUseCase(@Autowired OutputProjectKafkaProducerService kafkaProducerService,
                                    @Autowired OutputProjectService projectService,
                                    @Autowired RemoteOutputEmployeeAPIService employeeAPIService,
                                    @Autowired RemoteOutputCompanyAPIService companyAPIService){
        return new UseCase(kafkaProducerService,projectService,employeeAPIService, companyAPIService);
    }
}
