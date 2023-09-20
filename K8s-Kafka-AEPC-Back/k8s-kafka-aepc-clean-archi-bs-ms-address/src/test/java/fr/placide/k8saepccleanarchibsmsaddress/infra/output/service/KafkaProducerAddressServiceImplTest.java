package fr.placide.k8saepccleanarchibsmsaddress.infra.output.service;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper.AddressMapper;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.repository.AddressRepository;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.service.KafkaProducerAddressServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
class KafkaProducerAddressServiceImplTest {
    private final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest"));
    @Mock
    private KafkaTemplate<String, Address> kafkaTemplate;
    @Mock
    private AddressRepository addressRepositoryMock;
    @InjectMocks
    private KafkaProducerAddressServiceImpl underTest;
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;

    @BeforeEach
    void setUp() {
        kafkaContainer.start();
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        log.info("list of kafka container brokers: {}", bootstrapServers);
        System.setProperty("kafka.bootstrapAddress", bootstrapServers);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendKafkaAddressAddEvent() {
        //PREPARE
        Address address = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        Message<Address> message = MessageBuilder
                .withPayload(address)
                .setHeader(KafkaHeaders.TOPIC, topic1)
                .build();
        //EXECUTE
        kafkaTemplate.send(message);
        Address sentAddress = underTest.sendKafkaAddressAddEvent(address);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaTemplate, Mockito.atLeast(1)).send(message),
                () -> Assertions.assertNotNull(sentAddress));
    }

    @Test
    void sendKafkaAddressDeleteEvent() throws AddressNotFoundException {
        //PREPARE
        Address address = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        Message<Address> message = MessageBuilder
                .withPayload(address)
                .setHeader(KafkaHeaders.TOPIC, topic2)
                .build();
        String id = "uuid-1";
        AddressModel addressModel = AddressMapper.mapBeanToModel(address);
        //EXECUTE
        Mockito.when(addressRepositoryMock.findById(id)).thenReturn(Optional.of(addressModel));
        Address savedModel = underTest.sendKafkaAddressDeleteEvent(id);
        kafkaTemplate.send(message);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).findById(id),
                () -> Mockito.verify(kafkaTemplate, Mockito.atLeast(1)).send(message),
                () -> Assertions.assertNotNull(savedModel));
    }

    @Test
    void sendKafkaAddressEditEvent() throws AddressNotFoundException {
        //PREPARE
        Address address = new Address(
                UUID.randomUUID().toString(),
                44, "Rue Notre Dame des Victoires",
                74002, "Paris",
                "France");
        Message<Address> message = MessageBuilder
                .withPayload(address)
                .setHeader(KafkaHeaders.TOPIC, topic3)
                .build();
        String id = "uuid-1";
        AddressModel addressModel = AddressMapper.mapBeanToModel(address);
        AddressDto addressDto = AddressMapper.mapBeanBeanToDto(address);
        //EXECUTE
        Mockito.when(addressRepositoryMock.findById(id)).thenReturn(Optional.of(addressModel));
        Address sentAddress = underTest.sendKafkaAddressEditEvent(addressDto, id);
        kafkaTemplate.send(message);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaTemplate, Mockito.atLeast(1)).send(message),
                () -> Assertions.assertNotNull(sentAddress));
    }
}