package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.CompanyServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.EmployeeServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Service
public class OutputProjectKafkaProducerServiceImpl implements OutputProjectKafkaProducerService {
    private final KafkaTemplate<String, Project> kafkaTemplate;
    private final ProjectRepository repository;
    private final EmployeeServiceProxy employeeServiceProxy;
    private final CompanyServiceProxy companyServiceProxy;
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;

    public OutputProjectKafkaProducerServiceImpl(KafkaTemplate<String, Project> kafkaTemplate,
                                                 ProjectRepository repository,
                                                 @Qualifier(value = "employee-service-proxy") EmployeeServiceProxy employeeServiceProxy,
                                                 @Qualifier(value = "company-service-proxy") CompanyServiceProxy companyServiceProxy) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
        this.employeeServiceProxy = employeeServiceProxy;
        this.companyServiceProxy = companyServiceProxy;
    }

    private Message<?> buildKfakaMessage(Project project, String topic){
        return MessageBuilder.withPayload(project)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
    }
    @Override
    public Project produceKafkaEventProjectCreate(Project project) {
        kafkaTemplate.send(buildKfakaMessage(project,topic1));
        return project;
    }

    @Override
    public Project produceKafkaEventProjectDelete(String projectId) throws ProjectNotFoundException {
        ProjectModel model = repository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        Project bean = Mapper.fromTo(model);
        kafkaTemplate.send(buildKfakaMessage(bean,topic2));
        return bean;
    }

    @Override
    public Project produceKafkaEventProjectEdit(ProjectDto dto, String projectId) throws ProjectNotFoundException,
            RemoteEmployeeApiException, RemoteCompanyApiException {
        ProjectModel projectModel = repository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        Optional<EmployeeModel> employeeModel = employeeServiceProxy.loadRemoteApiGetEmployee(dto.getEmployeeId());
        if(employeeModel.isEmpty()){
            throw new RemoteEmployeeApiException();
        }
        Optional<CompanyModel> companyModel = companyServiceProxy.loadRemoteApiGetCompany(dto.getCompanyId());
        if(companyModel.isEmpty()){
            throw new RemoteCompanyApiException();
        }
        Project bean = Mapper.fromTo(dto);
        bean.setProjectId(projectModel.getProjectId());
        bean.setCreatedDate(Timestamp.from(Instant.now()).toString());
        kafkaTemplate.send(buildKfakaMessage(bean,topic3));
        return bean;
    }
}
