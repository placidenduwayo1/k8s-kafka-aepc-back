package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.service;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeServiceKafkaProducer;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.usecase.Validator;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy.AddressServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class OutputEmployeeServiceKafkaProducerImpl implements OutputEmployeeServiceKafkaProducer {
    private final KafkaTemplate<String, Employee> employeeKafkaTemplate;
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;
    private final EmployeeRepository repository;
    @Qualifier(value = "address-service-proxy")
    private final AddressServiceProxy addressServiceProxy;

    public OutputEmployeeServiceKafkaProducerImpl(KafkaTemplate<String, Employee> employeeKafkaTemplate,
                                                  EmployeeRepository repository,
                                                  @Qualifier(value = "address-service-proxy") AddressServiceProxy addressServiceProxy) {
        this.employeeKafkaTemplate = employeeKafkaTemplate;
        this.repository = repository;
        this.addressServiceProxy = addressServiceProxy;
    }

    private Message<?> buildMessage(Employee payload, String topic){
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
    }

    @Override
    public Employee produceKafkaEventEmployeeCreate(Employee employee) {
        Message<?> payload = buildMessage(employee,topic1);
        employeeKafkaTemplate.send(payload);
        return employee;
    }

    @Override
    public Employee produceKafkaEventEmployeeDelete(String employeeId) throws EmployeeNotFoundException {
       EmployeeModel model = repository.findById(employeeId).orElseThrow(
               EmployeeNotFoundException::new);

        Employee employee = EmployeeMapper.toBean(model);
        Message<?> payload = buildMessage(employee,topic2);

        employeeKafkaTemplate.send(payload);
        return employee;
    }

    @Override
    public Employee produceKafkaEventEmployeeEdit(EmployeeDto employeeDto, String employeeId) throws
            EmployeeNotFoundException, RemoteApiAddressNotLoadedException {
        EmployeeModel model = repository.findById(employeeId).orElseThrow(EmployeeNotFoundException::new);

        Employee employee = EmployeeMapper.fromDto(employeeDto);
        AddressModel addressModel = addressServiceProxy.loadRemoteApiGetAddressById(employee.getAddressId())
                .orElseThrow(RemoteApiAddressNotLoadedException::new);

        employee.setEmployeeId(employeeId);
        employee.setEmail(Validator.setEmail(employee.getFirstname(),employee.getLastname()));
        employee.setHireDate(model.getHireDate());
        employee.setAddressId(addressModel.getAddressId());
        Message<?> payload = buildMessage(employee,topic3);
        employeeKafkaTemplate.send(payload);
        return employee;
    }
}
