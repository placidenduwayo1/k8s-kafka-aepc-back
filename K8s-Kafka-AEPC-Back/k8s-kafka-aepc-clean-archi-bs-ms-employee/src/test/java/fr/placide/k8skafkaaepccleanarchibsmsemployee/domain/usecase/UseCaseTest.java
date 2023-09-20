package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeServiceKafkaProducer;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.RemoteOutputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class UseCaseTest {
    @Mock
    private OutputEmployeeServiceKafkaProducer kafkaProducerMock;
    @Mock
    private OutputEmployeeService employeeServiceMock;
    @Mock
    private RemoteOutputAddressService addressProxyMock;
    @InjectMocks
    private UseCase underTest;
    private static final String FIRSTNAME = "Placide";
    private static final String LASTNAME = "Nduwayo";
    private static final String STATE = "active";
    private static final String TYPE = "software-engineer";
    private Address remoteAddress;
    private static final String ADDRESS_ID = "uuid-address";
    private EmployeeDto dto;
    private Employee bean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        remoteAddress = new Address(ADDRESS_ID, 184, "Avenue de Liège", 59300, "Valenciennes", "France");
        dto = EmployeeDto.builder()
                .firstname(FIRSTNAME)
                .lastname(LASTNAME)
                .state(STATE)
                .type(TYPE)
                .addressId(ADDRESS_ID)
                .build();
        bean = EmployeeMapper.fromDto(dto);
        bean.setAddress(remoteAddress);
    }

    @Test
    void produceKafkaEventEmployeeCreate() throws EmployeeTypeInvalidException, EmployeeEmptyFieldsException,
            EmployeeStateInvalidException, RemoteApiAddressNotLoadedException, EmployeeAlreadyExistsException {
        //PREPARE
        //EXECUTE
        Mockito.when(addressProxyMock.getRemoteAddressById(ADDRESS_ID)).thenReturn(remoteAddress);
        Mockito.when(kafkaProducerMock.produceKafkaEventEmployeeCreate(Mockito.any(Employee.class))).thenReturn(bean);
        Employee produced = underTest.produceKafkaEventEmployeeCreate(dto);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Mockito.verify(kafkaProducerMock, Mockito.atLeast(1)).produceKafkaEventEmployeeCreate(Mockito.any()),
                () -> Mockito.verify(addressProxyMock, Mockito.atLeast(1)).getRemoteAddressById(ADDRESS_ID),
                () -> Assertions.assertEquals(produced, bean));
    }

    @Test
    void createEmployee() {
        //PREPARE
        //EXECUTE
        Mockito.when(employeeServiceMock.saveEmployee(bean)).thenReturn(bean);
        Employee actual = underTest.createEmployee(bean);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).saveEmployee(bean),
                () -> Assertions.assertNotNull(actual),
                () -> Assertions.assertEquals(bean, actual),
                () -> Assertions.assertNotNull(actual.getAddress()));
    }

    @Test
    void produceKafkaEventEmployeeDelete() throws EmployeeNotFoundException {
        //PREPARE
        String employeeId = "uuid";
        //EXECUTE
        Mockito.when(employeeServiceMock.getEmployeeById(employeeId)).thenReturn(Optional.of(bean));
        Employee actual = underTest.getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);
        Mockito.when(kafkaProducerMock.produceKafkaEventEmployeeDelete(employeeId)).thenReturn(bean);
        Employee produced = underTest.produceKafkaEventEmployeeDelete(employeeId);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).getEmployeeById(employeeId),
                () -> Mockito.verify(kafkaProducerMock, Mockito.atLeast(1)).produceKafkaEventEmployeeDelete(employeeId),
                () -> Assertions.assertEquals(actual, bean),
                () -> Assertions.assertEquals(produced, bean),
                () -> Assertions.assertEquals(actual, produced));
    }

    @Test
    void deleteEmployee() throws EmployeeNotFoundException {
        //PREPARE
        String id = "uuid";
        //EXECUTE
        Mockito.when(employeeServiceMock.getEmployeeById(id)).thenReturn(Optional.of(bean));
        Employee employee = underTest.getEmployeeById(id).orElseThrow(EmployeeNotFoundException::new);
        Mockito.when(employeeServiceMock.deleteEmployee(employee.getEmployeeId())).thenReturn("");
        String msg = underTest.deleteEmployee(id);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).deleteEmployee(employee.getEmployeeId()),
                () -> Assertions.assertNotNull(employee),
                () -> Assertions.assertEquals("Employee" + employee + "successfully deleted", msg),
                () -> Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).getEmployeeById(id));
    }

    @Test
    void produceKafkaEventEmployeeEdit() throws EmployeeNotFoundException, RemoteApiAddressNotLoadedException, EmployeeTypeInvalidException, EmployeeEmptyFieldsException, EmployeeStateInvalidException {
        //PREPARE
        String employeeId = "uuid";
        //EXECUTE
        Mockito.when(addressProxyMock.getRemoteAddressById(ADDRESS_ID)).thenReturn(remoteAddress);
        Mockito.when(kafkaProducerMock.produceKafkaEventEmployeeEdit(dto, employeeId)).thenReturn(bean);
        Employee actual = underTest.produceKafkaEventEmployeeEdit(dto, employeeId);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(addressProxyMock, Mockito.atLeast(1)).getRemoteAddressById(ADDRESS_ID);
            Mockito.verify(kafkaProducerMock, Mockito.atLeast(1)).produceKafkaEventEmployeeEdit(dto,employeeId);
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(bean, actual);
        });
    }
    @Test
    void editEmployee(){
        //PREPARE
        Employee updated = bean;
        updated.setAddress(new Address("address-Paris",44,"Rue Notre Dame des Victoires",74002,"Paris","France"));
        //EXECUTE
        Mockito.when(employeeServiceMock.editEmployee(bean)).thenReturn(updated);
        Employee actual = underTest.editEmployee(bean);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).editEmployee(bean);
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(updated, actual);
        });
    }
    @Test
    void loadEmployeesByRemoteAddress() throws RemoteApiAddressNotLoadedException {
        //PREPARE
        List<Employee> beans = List.of(bean,bean,bean);
        //EXECUTE
        Mockito.when(employeeServiceMock.loadEmployeesByRemoteAddress(ADDRESS_ID)).thenReturn(beans);
        Mockito.when(addressProxyMock.getRemoteAddressById(ADDRESS_ID)).thenReturn(remoteAddress);
        List<Employee> actuals = underTest.loadEmployeesByRemoteAddress(ADDRESS_ID);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).loadEmployeesByRemoteAddress(ADDRESS_ID);
            Mockito.verify(addressProxyMock, Mockito.atLeast(1)).getRemoteAddressById(ADDRESS_ID);
            Assertions.assertNotNull(actuals);
            Assertions.assertEquals(beans, actuals);
        });
    }
    @Test
    void loadAllEmployees(){
        //PREPARE
        List<Employee> beans = List.of(bean,bean,bean);
        //EXECUTE
        Mockito.when(employeeServiceMock.loadAllEmployees()).thenReturn(beans);
        List<Employee> actuals = underTest.loadAllEmployees();
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(employeeServiceMock, Mockito.atLeast(1)).loadAllEmployees();
            Assertions.assertNotNull(actuals);
            Assertions.assertEquals(beans.size(), actuals.size());
            actuals.forEach((var empl)-> Assertions.assertNotNull(empl.getAddress()));
        });
    }
    @Test
    void loadRemoteAllAddresses(){
        //PREPARE
        List<Address> addresses = List.of(remoteAddress, remoteAddress, remoteAddress);
        //EXECUTE
        Mockito.when(addressProxyMock.loadAllRemoteAddresses()).thenReturn(addresses);
        List<Address> actuals = underTest.loadRemoteAllAddresses();
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(addressProxyMock, Mockito.atLeast(1)).loadAllRemoteAddresses();
            Assertions.assertNotNull(actuals);
            Assertions.assertEquals(addresses.size(), actuals.size());
            actuals.forEach((var address)-> Assertions.assertEquals("Valenciennes",address.getCity()));
        });
    }
}