package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.service;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.RemoteOutputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.address.AddressMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy.AddressServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class OutputEmployeeServiceImpl implements  OutputEmployeeService, RemoteOutputAddressService {
    private final EmployeeRepository repository;
    @Qualifier(value = "address-service-proxy")
    private final AddressServiceProxy addressProxy;

    public OutputEmployeeServiceImpl(EmployeeRepository repository,
                                     @Qualifier(value = "address-service-proxy")AddressServiceProxy addressProxy) {
        this.repository = repository;
        this.addressProxy = addressProxy;
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic1}",groupId = "${spring.kafka.consumer.group-id}")
    public Employee consumeKafkaEventEmployeeCreate(@Payload Employee employee,
                                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
       log.info("employee to add: <{}> consumed from topic: <{}>",employee,topic);
        return employee;
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        Employee consumed = consumeKafkaEventEmployeeCreate(employee,"${topics.names.topic1}");
        EmployeeModel savedEmployee = repository.save(EmployeeMapper.toModel(consumed));
        return EmployeeMapper.toBean(savedEmployee);
    }

    private List<Employee> employeeModelToBean(List<EmployeeModel> models){
        return models.stream()
                .map(EmployeeMapper::toBean)
                .toList();
    }
    @Override
    public Optional<Employee> getEmployeeById(String employeeId) throws EmployeeNotFoundException {
        EmployeeModel model = repository.findById(employeeId).orElseThrow(
                EmployeeNotFoundException::new);
        return Optional.of(EmployeeMapper.toBean(model));
    }

    @Override
    public List<Employee> loadEmployeesByRemoteAddress(String addressId) throws RemoteApiAddressNotLoadedException {
        AddressModel model= addressProxy.loadRemoteApiGetAddressById(addressId).orElseThrow(RemoteApiAddressNotLoadedException::new);
        List<EmployeeModel> models = repository.findByAddressId(model.getAddressId());
        return employeeModelToBean(models);
    }

    @Override
    public List<Employee> loadEmployeeByInfo(String firstname, String lastname, String state, String type,String addressId) {
        return employeeModelToBean(repository
                .findByFirstnameAndLastnameAndStateAndTypeAndAddressId(firstname,lastname,state,type,addressId)
        );
    }

    @Override
    public List<Employee> loadAllEmployees() {
        return employeeModelToBean(repository.findAll());
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic2}", groupId = "${spring.kafka.consumer.group-id}")
    public Employee consumeKafkaEventEmployeeDelete(@Payload Employee employee,
                                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("employee to delete:<{}> consumed from topic:<{}>",employee,topic);
       return employee;
    }

    @Override
    public String deleteEmployee(String employeeId) throws EmployeeNotFoundException {
        Employee bean = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);
        Employee consumed = consumeKafkaEventEmployeeDelete(bean,"${topics.names.topic2}");
        repository.deleteById(consumed.getEmployeeId());
        return "employee <"+consumed+"> is deleted";
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic3}", groupId = "${spring.kafka.consumer.group-id}")
    public Employee consumeKafkaEventEmployeeEdit(@Payload Employee employee,
                                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("employee to update :<{}> consumed from topic:<{}>",employee, topic);
        return employee;
    }

    @Override
    public Employee editEmployee(Employee employee) {
        Employee consumed = consumeKafkaEventEmployeeEdit(employee,"${topics.names.topic3}");
        EmployeeModel mapped = EmployeeMapper.toModel(consumed);
        return EmployeeMapper.toBean(repository.save(mapped));
    }

    @Override
    public Address getRemoteAddressById(String addressId) throws RemoteApiAddressNotLoadedException {
        return AddressMapper.toBean(addressProxy.loadRemoteApiGetAddressById(addressId)
                .orElseThrow(RemoteApiAddressNotLoadedException::new));
    }

    @Override
    public List<Address> loadAllRemoteAddresses() {
        List<AddressModel> models = addressProxy.loadRemoteAddressIpiGetAllAddresses();
        return models.stream()
                .map(AddressMapper::toBean)
                .toList();
    }
}
