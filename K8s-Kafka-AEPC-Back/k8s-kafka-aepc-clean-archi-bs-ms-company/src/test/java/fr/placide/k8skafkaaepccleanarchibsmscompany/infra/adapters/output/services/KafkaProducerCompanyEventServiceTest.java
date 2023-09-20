package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.mapper.CompanyMapper;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.repository.CompanyRepository;
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
import java.util.UUID;

@Slf4j
class KafkaProducerCompanyEventServiceTest {
    private final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.
            parse("confluentinc/cp-kafka:latest"));
    @Mock
    private KafkaTemplate<String, Company> companyKafkaTemplate;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private KafkaProducerCompanyEventService underTest;
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";
    private static final String TOPIC3 = "topic3";

    @BeforeEach
    void setUp() {
        kafkaContainer.start();
        MockitoAnnotations.openMocks(this);
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        log.info("list of kafka container brokers: {}", bootstrapServers);
        System.setProperty("kafka.bootstrapAddress", bootstrapServers);
    }

    @Test
    void produceKafkaEventCompanyCreate() {
        //PREPARE
        Company company = new Company(
                UUID.randomUUID().toString(), "company-name", "lille", "esn", Timestamp.from(Instant.now()).toString());

        Message<Company> message = MessageBuilder
                .withPayload(company)
                .setHeader(KafkaHeaders.TOPIC, TOPIC1)
                .build();
        //EXECUTE
        companyKafkaTemplate.send(message);
        Company sent = underTest.produceKafkaEventCompanyCreate(company);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(companyKafkaTemplate, Mockito.atLeast(1)).send(message),
                () -> Assertions.assertNotNull(sent));
    }

    @Test
    void produceKafkaEventCompanyDelete() throws CompanyNotFoundException {
        //PREPARE
        Company company = new Company(
                UUID.randomUUID().toString(), "company-name", "lille", "esn", Timestamp.from(Instant.now()).toString());
        Message<Company> message = MessageBuilder
                .withPayload(company)
                .setHeader(KafkaHeaders.TOPIC, TOPIC2)
                .build();
        String id = "uuid-1";
        CompanyModel model = CompanyMapper.fromBeanToModel(company);
        //EXECUTE
        Mockito.when(companyRepository.findById(id)).thenReturn(Optional.of(model));
        Company savedModel = underTest.produceKafkaEventCompanyDelete(id);
        companyKafkaTemplate.send(message);
        //VERIFY
        Assertions.assertAll("gpe of assertions", ()->{
            Mockito.verify(companyRepository, Mockito.atLeast(1)).findById(id);
            Mockito.verify(companyKafkaTemplate, Mockito.atLeast(1)).send(message);
            Assertions.assertNotNull(savedModel);
        });
    }

    @Test
    void produceKafkaEventCompanyEdit() throws CompanyNotFoundException {
        //PREPARE
        Company company = new Company(
                UUID.randomUUID().toString(), "company-name", "lille", "esn", Timestamp.from(Instant.now()).toString());
        Message<Company> message = MessageBuilder
                .withPayload(company)
                .setHeader(KafkaHeaders.TOPIC, TOPIC3)
                .build();
        String id = "uuid-1";

        CompanyModel model = CompanyMapper.fromBeanToModel(company);
        CompanyDto dto = CompanyMapper.fromBeanToDto(company);
        //EXECUTE
        Mockito.when(companyRepository.findById(id)).thenReturn(Optional.of(model));
        Company sent = underTest.produceKafkaEventCompanyEdit(dto, id);
        companyKafkaTemplate.send(message);
        //VERIFY
        Mockito.verify(companyKafkaTemplate, Mockito.atLeast(1)).send(message);
        Assertions.assertNotNull(sent);
    }
}