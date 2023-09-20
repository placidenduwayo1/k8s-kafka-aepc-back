package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.CompanyServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.EmployeeServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

@Slf4j
class OutputProjectKafkaProducerServiceImplTest {
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));
    @Mock
    private ProjectRepository repositoryMock;
    @Mock
    private KafkaTemplate<String, Project> kafkaTemplateMock;
    @Mock
    private EmployeeServiceProxy employeeServiceProxyMock;
    @Mock
    private CompanyServiceProxy companyServiceProxyMock;
    @InjectMocks
    private OutputProjectKafkaProducerServiceImpl underTest;
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";
    private static final String TOPIC3 = "topic3";
    private static final String EMPLOYEE_ID = "uuid-e";
    private static final String COMPANY_ID = "uuid-c";
    private Project project;
    private Employee remoteEmployee;
    private Company remoteCompany;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        KAFKA_CONTAINER.start();
        String brokers = KAFKA_CONTAINER.getBootstrapServers();
        log.info("list of kafka container brokers: {}", brokers);
        System.setProperty("brokers", brokers);
        remoteEmployee = new Employee(EMPLOYEE_ID, "Placide", "Nduwayo", "pnduwayo@gmail.com", Timestamp.from(Instant.now()).toString(),
                "active", "software-engineer");
        remoteCompany = new Company(COMPANY_ID, "Natan", "Paris", "esn", "2020-10-20:00:00:00");
        project = new Project("uuid", "Guppy", "outil d'aide au business analyse de la production des besoins techniques",
                1, "ongoing", Timestamp.from(Instant.now()).toString(), EMPLOYEE_ID, remoteEmployee, COMPANY_ID, remoteCompany);

    }

    @Test
    void produceKafkaEventProjectCreate() {
        //PREPARE
        Message<?> message = buildKafkaMessage(project, TOPIC1);
        //EXECUTE
        kafkaTemplateMock.send(message);
        Project sent = underTest.produceKafkaEventProjectCreate(project);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaTemplateMock).send(message),
                () -> Assertions.assertNotNull(sent));
    }

    @Test
    void produceKafkaEventProjectDelete() throws ProjectNotFoundException {
        //PREPARE
        String projectId = "uuid";
        ProjectModel model = Mapper.fromTo(project);
        Message<?> message = buildKafkaMessage(project, TOPIC2);
        //EXECUTE
        Mockito.when(repositoryMock.findById(projectId)).thenReturn(Optional.of(model));
        Project bean = underTest.produceKafkaEventProjectDelete(projectId);
        bean.setCreatedDate(project.getCreatedDate());
        kafkaTemplateMock.send(message);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaTemplateMock).send(message),
                () -> Mockito.verify(repositoryMock).findById(projectId),
                ()->Assertions.assertNotNull(bean));
    }

    @Test
    void produceKafkaEventProjectEdit() throws ProjectNotFoundException, RemoteCompanyApiException, RemoteEmployeeApiException {
        //PREPARE
        Message<?> message = buildKafkaMessage(project,TOPIC3);
        EmployeeModel eModel = Mapper.fromTo(remoteEmployee);
        CompanyModel cModel = Mapper.fromTo(remoteCompany);
        ProjectDto dto = Mapper.fromBeanToDto(project);
        ProjectModel pModel = Mapper.fromTo(project);
        String projectId="uuid";
        //EXECUTE
        Mockito.when(employeeServiceProxyMock.loadRemoteApiGetEmployee(dto.getEmployeeId())).thenReturn(Optional.of(eModel));
        Mockito.when(companyServiceProxyMock.loadRemoteApiGetCompany(dto.getCompanyId())).thenReturn(Optional.of(cModel));
        Mockito.when(repositoryMock.findById(projectId)).thenReturn(Optional.of(pModel));
        Project toDelete = underTest.produceKafkaEventProjectEdit(dto,projectId);
        kafkaTemplateMock.send(message);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                ()->Mockito.verify(employeeServiceProxyMock).loadRemoteApiGetEmployee(dto.getEmployeeId()),
                ()->Mockito.verify(companyServiceProxyMock).loadRemoteApiGetCompany(dto.getCompanyId()),
                ()->Mockito.verify(repositoryMock).findById(projectId),
                ()->Assertions.assertNotNull(toDelete));
    }

    private Message<?> buildKafkaMessage(Project payload, String topic) {
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
    }
}