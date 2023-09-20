package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.output.service;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy.AddressServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.repository.EmployeeRepository;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.service.OutputEmployeeServiceKafkaProducerImpl;
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
class OutputEmployeeServiceKafkaProducerImplTest {
    private final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.
            parse("confluentinc/cp-kafka:latest"));
    @Mock
    private KafkaTemplate<String, Employee> employeeKafkaTemplate;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private AddressServiceProxy addressServiceProxy;
    @InjectMocks
    private OutputEmployeeServiceKafkaProducerImpl underTest;
    private static final String TOPIC1="topic1";
    private static final String TOPIC2="topic2";
    private static final String TOPIC3="topic3";
    @BeforeEach
    void setUp() {
        kafkaContainer.start();
        MockitoAnnotations.openMocks(this);
        String bootstrapServers = kafkaContainer.getBootstrapServers();
        log.info("list of kafka container brokers: {}",bootstrapServers);
        System.setProperty("kafka.bootstrapAddress", bootstrapServers);
    }

    @Test
    void produceKafkaEventEmployeeCreate() {
        //PREPARE
        Employee employee = new Employee(
                UUID.randomUUID().toString(),"Placide","Nduwayo","placide.nduwayo@domain.com",
                Timestamp.from(Instant.now()).toString(), "active","software-engineer","null-address-id",new Address());

        Message<?> message = buildKafkaMessage(employee,TOPIC1);
        //EXECUTE
        employeeKafkaTemplate.send(message);
        Employee sentEmployee = underTest.produceKafkaEventEmployeeCreate(employee);
        //VERIFY
        Mockito.verify(employeeKafkaTemplate).send(message);
        Assertions.assertNotNull(sentEmployee);
    }

    @Test
    void produceKafkaEventEmployeeDelete() throws EmployeeNotFoundException {
        //PREPARE
        Employee employee = new Employee(
                UUID.randomUUID().toString(),"Placide","Nduwayo","placide.nduwayo@domain.com",
                Timestamp.from(Instant.now()).toString(), "active","software-engineer","null-address-id",new Address());
        Message<?> message = buildKafkaMessage(employee,TOPIC2);
        String id = "uuid-1";
        EmployeeModel employeeModel = EmployeeMapper.toModel(employee);
        //EXECUTE
        Mockito.when(employeeRepository.findById(id)).thenReturn(Optional.of(employeeModel));
        Employee savedModel = underTest.produceKafkaEventEmployeeDelete(id);
        employeeKafkaTemplate.send(message);
        //VERIFY
        Mockito.verify(employeeRepository).findById(id);
        Mockito.verify(employeeKafkaTemplate).send(message);
        Assertions.assertNotNull(savedModel);
    }

    @Test
    void produceKafkaEventEmployeeEdit() throws RemoteApiAddressNotLoadedException, EmployeeNotFoundException {
        //PREPARE
        Employee employee = new Employee(
                UUID.randomUUID().toString(),"Placide","Nduwayo","placide.nduwayo@domain.com",
                Timestamp.from(Instant.now()).toString(), "active","software-engineer","null-address-id",new Address());
        Message<?> message = buildKafkaMessage(employee,TOPIC3);
        String employeeId = "uuid-1";
        String addressId ="uuid-2";
        AddressModel addressModel = new AddressModel(addressId,184, "Avenue de Liège",59300,
                "Valenciennes","France");
        EmployeeModel employeeModel = EmployeeMapper.toModel(employee);
        EmployeeDto dto = EmployeeMapper.fromBeanToDto(employee);
        //EXECUTE
        Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employeeModel));
        Mockito.when(addressServiceProxy.loadRemoteApiGetAddressById(employee.getAddressId())).thenReturn(Optional.of(addressModel));
        Employee sentEmployee= underTest.produceKafkaEventEmployeeEdit(dto, employeeId);
        employeeKafkaTemplate.send(message);
        //VERIFY
        Mockito.verify(employeeKafkaTemplate).send(message);
        Assertions.assertNotNull(sentEmployee);
    }

    private Message<?> buildKafkaMessage(Employee payload, String topic){
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
    }
}