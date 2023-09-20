package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input.RemoteInputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input.InputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeServiceKafkaProducer;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.RemoteOutputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output.OutputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee.EmployeeMapper;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UseCase implements InputEmployeeService, RemoteInputAddressService {
    private final OutputEmployeeServiceKafkaProducer outputEmployeeServiceKafkaProducer;
    private final OutputEmployeeService outputEmployeeService;
    private final RemoteOutputAddressService remoteOutputAddressService;


    public UseCase(OutputEmployeeServiceKafkaProducer outputEmployeeServiceKafkaProducer,
                   OutputEmployeeService outputEmployeeService,
                   RemoteOutputAddressService remoteOutputAddressService) {
        this.outputEmployeeServiceKafkaProducer = outputEmployeeServiceKafkaProducer;
        this.outputEmployeeService = outputEmployeeService;
        this.remoteOutputAddressService = remoteOutputAddressService;
    }

    private void checkEmployeeValidity(EmployeeDto employeeDto) throws
            EmployeeEmptyFieldsException, EmployeeStateInvalidException,
            EmployeeTypeInvalidException, RemoteApiAddressNotLoadedException {
        if(!Validator.isValidEmployee(
                employeeDto.getFirstname(), employeeDto.getLastname(),
                employeeDto.getState(), employeeDto.getType(),
                employeeDto.getAddressId()) ) {
            throw new EmployeeEmptyFieldsException();
        } else if(!Validator.checkStateValidity(employeeDto.getState())){
            throw new EmployeeStateInvalidException();
        } else if (!Validator.checkTypeValidity(employeeDto.getType())) {
            throw new EmployeeTypeInvalidException();
        }

        Address address = getRemoteAddressById(employeeDto.getAddressId());
        if(Validator.remoteAddressApiUnreachable(address.getAddressId())){
            throw new RemoteApiAddressNotLoadedException();
        }
    }

    private void checkEmployeeAlreadyExist(EmployeeDto dto) throws EmployeeAlreadyExistsException {
        if(!loadEmployeeByInfo(dto.getFirstname(), dto.getLastname(), dto.getState(), dto.getType(),
                dto.getAddressId()).isEmpty()){
            throw new EmployeeAlreadyExistsException();
        }
    }
    //kafka producer employee create, edit and delete events
    @Override
    public Employee produceKafkaEventEmployeeCreate(EmployeeDto employeeDto) throws
            EmployeeTypeInvalidException, EmployeeEmptyFieldsException,
            EmployeeStateInvalidException, RemoteApiAddressNotLoadedException,
            EmployeeAlreadyExistsException {
        Validator.formatter(employeeDto);
        checkEmployeeValidity(employeeDto);
        checkEmployeeAlreadyExist(employeeDto);
        Employee employee = EmployeeMapper.fromDto(employeeDto);
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setHireDate(Timestamp.from(Instant.now()).toString());
        employee.setEmail(Validator.setEmail(employee.getFirstname(), employee.getLastname()));
        employee.setAddress(getRemoteAddressById(employeeDto.getAddressId()));
        return outputEmployeeServiceKafkaProducer.produceKafkaEventEmployeeCreate(employee);
    }
    @Override
    public Employee createEmployee(Employee employee) {
        return outputEmployeeService.saveEmployee(employee);
    }
    @Override
    public Employee produceKafkaEventEmployeeDelete(String employeeId) throws EmployeeNotFoundException {
       return outputEmployeeServiceKafkaProducer.produceKafkaEventEmployeeDelete(employeeId);
    }

    @Override
    public String deleteEmployee(String employeeId) throws EmployeeNotFoundException {
       Employee employee = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);
       outputEmployeeService.deleteEmployee(employee.getEmployeeId());
       return "Employee"+employee+"successfully deleted";
    }

    @Override
    public Employee produceKafkaEventEmployeeEdit(EmployeeDto employeeDto, String employeeId) throws
            RemoteApiAddressNotLoadedException, EmployeeNotFoundException, EmployeeTypeInvalidException, EmployeeEmptyFieldsException,
            EmployeeStateInvalidException {
        Validator.formatter(employeeDto);
        checkEmployeeValidity(employeeDto);
        return outputEmployeeServiceKafkaProducer.produceKafkaEventEmployeeEdit(employeeDto,employeeId);
    }

    @Override
    public Employee editEmployee(Employee payload) {
        return outputEmployeeService.editEmployee(payload);
    }

    @Override
    public List<Employee> loadEmployeesByRemoteAddress(String addressId) throws RemoteApiAddressNotLoadedException {
        Address address = remoteOutputAddressService.getRemoteAddressById(addressId);
        if(address == null){
            throw new RemoteApiAddressNotLoadedException();
        }
        return outputEmployeeService.loadEmployeesByRemoteAddress(address.getAddressId());
    }

    @Override
    public Optional<Employee> getEmployeeById(String employeeId) throws EmployeeNotFoundException {
      return Optional.of(outputEmployeeService.getEmployeeById(employeeId)).orElseThrow(
              EmployeeNotFoundException::new);
    }

    @Override
    public List<Employee> loadEmployeeByInfo(String firstname, String lastname, String state, String type, String addressId) {
        return outputEmployeeService.loadEmployeeByInfo(firstname, lastname,state,type,addressId);
    }

    @Override
    public List<Employee> loadAllEmployees() {
        return outputEmployeeService.loadAllEmployees();
    }

    @Override
    public Address getRemoteAddressById(String addressId) throws RemoteApiAddressNotLoadedException {
        Address address = remoteOutputAddressService.getRemoteAddressById(addressId);
        if(address==null){
            throw new RemoteApiAddressNotLoadedException();
        }
        return address;
    }

    @Override
    public List<Address> loadRemoteAllAddresses() {
        return remoteOutputAddressService.loadAllRemoteAddresses();
    }
}
