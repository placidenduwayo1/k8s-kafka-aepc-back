package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.output.service;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.usecase.Validator;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy.AddressServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.address.AddressMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.repository.EmployeeRepository;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.service.OutputEmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


class OutputEmployeeServiceImplTest {
    @Mock
    private EmployeeRepository repository;
    @Mock
    private AddressServiceProxy proxy;
    @InjectMocks
    private OutputEmployeeServiceImpl underTest;
    private Employee employee;
    private Address address;
    private static final String addressId = "uuid";
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";
    private static final String TOPIC3 = "topic3";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String nom = "nduwayo";
        String prenom = "placide";
        address = new Address(addressId, 184, "Avenue de Liège", 59300, "Valenciennes", "France");
        employee = new Employee(UUID.randomUUID().toString(), prenom, nom, Validator.setEmail(prenom, nom), Timestamp.from(Instant.now()).toString(),"active",
                "software-engineer",  addressId, address);
    }

    @Test
    void consumeKafkaEventEmployeeCreate() {
        //PREPARE
        //EXECUTE
        Employee consumed = underTest.consumeKafkaEventEmployeeCreate(employee, TOPIC1);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertNotNull(consumed),
                () -> Assertions.assertEquals(consumed, employee));
    }

    @Test
    void saveEmployee() {
        //PREPARE
        EmployeeModel model = EmployeeMapper.toModel(employee);
        //EXECUTE
        Employee consumed = underTest.consumeKafkaEventEmployeeCreate(employee, TOPIC1);
        EmployeeModel consumedModel = EmployeeMapper.toModel(consumed);
        Mockito.when(repository.save(consumedModel)).thenReturn(model);
        Employee saved = underTest.saveEmployee(consumed);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Mockito.verify(repository).save(consumedModel),
                ()->Assertions.assertNotNull(saved));
    }

    @Test
    void getEmployeeById() throws EmployeeNotFoundException {
        //PREPARE
        String employeeId="uuid";
        EmployeeModel model =EmployeeMapper.toModel(employee);
        //EXECUTE
        Mockito.when(repository.findById(employeeId)).thenReturn(Optional.of(model));
        Employee obtained = underTest.getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Mockito.verify(repository).findById(employeeId), ()->Assertions.assertNotNull(obtained));
    }

    @Test
    void loadEmployeesByRemoteAddress() throws RemoteApiAddressNotLoadedException {
        //PREPARE
        AddressModel addressModel = AddressMapper.toModel(address);
        List<EmployeeModel> employeeModels = List.of(EmployeeMapper.toModel(employee));
        //EXECUTE
        Mockito.when(proxy.loadRemoteApiGetAddressById(addressId)).thenReturn(Optional.of(addressModel));
        Mockito.when(repository.findByAddressId(addressModel.getAddressId())).thenReturn(employeeModels);
        List<Employee> employees = underTest.loadEmployeesByRemoteAddress(addressId);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Assertions.assertEquals(1,employees.size()),
                ()->Mockito.verify(proxy).loadRemoteApiGetAddressById(addressId),
                ()->Mockito.verify(repository).findByAddressId(addressId));
    }

    @Test
    void loadEmployeeByInfo() {
        //PREPARE
        String firstname="Placide";
        String lastname ="Nduwayo";
        String state ="active";
        String type ="software-engineer";
        EmployeeModel model = EmployeeMapper.toModel(employee);
        //EXECUTE
        Mockito.when(repository.findByFirstnameAndLastnameAndStateAndTypeAndAddressId(firstname,lastname,state,type,addressId))
                .thenReturn(List.of(model));
        List<Employee> employees = underTest.loadEmployeeByInfo(firstname,lastname,state,type,addressId);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Assertions.assertEquals(1,employees.size()),
                ()->Mockito.verify(repository).findByFirstnameAndLastnameAndStateAndTypeAndAddressId(firstname,lastname,state,type,addressId)
        );
    }

    @Test
    void loadAllEmployees() {
        //PREPARE
        EmployeeModel model = EmployeeMapper.toModel(employee);
        //EXECUTE
        Mockito.when(repository.findAll()).thenReturn(List.of(model));
        List<Employee>employees = underTest.loadAllEmployees();
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Assertions.assertEquals(1,employees.size()),
                ()->Mockito.verify(repository).findAll()
        );
    }

    @Test
    void consumeKafkaEventEmployeeDelete() {
        //PREPARE

        //EXECUTE
        Employee consumed = underTest.consumeKafkaEventEmployeeDelete(employee,TOPIC2);
        //VERIFY
        Assertions.assertEquals(consumed, employee);
    }

    @Test
    void deleteEmployee() throws EmployeeNotFoundException {
        //PREPARE
        String employeeId ="uuid";
        EmployeeModel employeeModel = EmployeeMapper.toModel(employee);
        //EXECUTE
        Mockito.when(repository.findById(employeeId)).thenReturn(Optional.of(employeeModel));
        Employee obtained = underTest.getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);
        Employee consumed = underTest.consumeKafkaEventEmployeeDelete(obtained,TOPIC2);
        String msg = underTest.deleteEmployee(employeeId);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Assertions.assertNotNull(obtained),
                ()->Assertions.assertEquals(consumed, obtained),
                ()->Assertions.assertEquals(msg,"employee <"+consumed+"> is deleted"));
    }

    @Test
    void consumeKafkaEventEmployeeEdit() {
        //PREPARE
        //EXECUTE
        Employee consumed = underTest.consumeKafkaEventEmployeeEdit(employee,TOPIC3);
        //VERIFY
        Assertions.assertNotNull(consumed);
    }

    @Test
    void editEmployee() throws EmployeeNotFoundException {
        //PREPARE
        String firstname="Placide";
        String lastname ="Nduwayo";
        Employee updated = new Employee(employee.getEmployeeId(),firstname, lastname, Validator.setEmail(firstname, lastname),
                employee.getHireDate(),"active", "software-engineer",  addressId, address);
        EmployeeModel model = EmployeeMapper.toModel(updated);
        String id="uuid";
        //EXECUTE
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(model));
        Employee obtained = underTest.getEmployeeById(id).orElseThrow(EmployeeNotFoundException::new);
        Employee consumed = underTest.consumeKafkaEventEmployeeEdit(obtained,TOPIC3);
        Mockito.when(repository.save(EmployeeMapper.toModel(consumed))).thenReturn(model);
        Employee saved = underTest.editEmployee(consumed);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Mockito.verify(repository).findById(id),
                ()->Mockito.verify(repository).save(EmployeeMapper.toModel(consumed)),
                ()->Assertions.assertNotNull(saved)
        );
    }

    @Test
    void getRemoteAddressById() throws RemoteApiAddressNotLoadedException {
        //PREPARE
        AddressModel model = AddressMapper.toModel(address);
        //EXECUTE
        Mockito.when(proxy.loadRemoteApiGetAddressById(addressId)).thenReturn(Optional.of(model));
        Address obtained = underTest.getRemoteAddressById(addressId);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Mockito.verify(proxy).loadRemoteApiGetAddressById(addressId),
                ()->Assertions.assertNotNull(obtained));
    }

    @Test
    void loadAllRemoteAddresses() {
        //PREPARE
        List<Address> addresses = List.of(address,address,address);
        List<AddressModel> models = addresses.stream().map(AddressMapper::toModel).toList();
        //EXECUTE
        Mockito.when(proxy.loadRemoteAddressIpiGetAllAddresses()).thenReturn(models);
        List<Address> obtained = underTest.loadAllRemoteAddresses();
        //VERIFY
        Assertions.assertAll("grp of assertions",
                ()->Mockito.verify(proxy).loadRemoteAddressIpiGetAllAddresses(),
                ()->Assertions.assertFalse(obtained.isEmpty()),
                ()->Assertions.assertEquals(3,obtained.size()));
    }
}