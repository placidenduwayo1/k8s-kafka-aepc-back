package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.controller;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input.InputEmployeeService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input.RemoteInputAddressService;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class EmployeeController {
    private final InputEmployeeService inputEmployeeService;
    private final RemoteInputAddressService remoteInputAddressService;

    public EmployeeController(InputEmployeeService inputEmployeeService, RemoteInputAddressService remoteInputAddressService) {
        this.inputEmployeeService = inputEmployeeService;
        this.remoteInputAddressService = remoteInputAddressService;
    }

    @PostMapping(value = "/employees")
    public ResponseEntity<Object> produceConsumeAndSaveEmployee(@RequestBody EmployeeDto employeeDto) throws
            EmployeeTypeInvalidException, EmployeeEmptyFieldsException, EmployeeStateInvalidException,
            RemoteApiAddressNotLoadedException, EmployeeAlreadyExistsException {

       Employee consumed = inputEmployeeService.produceKafkaEventEmployeeCreate(employeeDto);
       Employee saved = inputEmployeeService.createEmployee(consumed);
       Address address = remoteInputAddressService.getRemoteAddressById(consumed.getAddressId());
       consumed.setAddress(address);
       saved.setAddress(address);
        return new ResponseEntity<>(String
                .format("%s is sent and consumed;%n %s is saved in db", consumed, saved),
                HttpStatus.OK);
    }
    @GetMapping(value = "/employees/addresses/id/{addressId}")
    public Address getRemoteAddress(@PathVariable(name = "addressId") String addressId) throws
            RemoteApiAddressNotLoadedException{
        return remoteInputAddressService.getRemoteAddressById(addressId);
    }
    @GetMapping(value = "/employees/addresses")
    public List<Address> getRemoteAddresses(){
       return remoteInputAddressService.loadRemoteAllAddresses();
    }
    @GetMapping(value = "/employees/addresses/{addressId}")
    public List<Employee> loadEmployeesOnGivenAddress(@PathVariable(name = "addressId") String addressId) throws
            RemoteApiAddressNotLoadedException {
        List<Employee> employees = inputEmployeeService.loadEmployeesByRemoteAddress(addressId);
        return setAddressToEmployee(employees);
    }
    @GetMapping(value = "/employees")
    public List<Employee> loadAllEmployees(){
        List<Employee> employees = inputEmployeeService.loadAllEmployees();
        return setAddressToEmployee(employees);
    }
    @GetMapping(value = "/employees/{id}")
    public Employee getEmployee(@PathVariable(name = "id") String id) throws EmployeeNotFoundException {
        return inputEmployeeService.getEmployeeById(id).orElseThrow(EmployeeNotFoundException::new);
    }
    @DeleteMapping(value = "/employees/{id}")
    public ResponseEntity<Object> delete(@PathVariable(name = "id") String id) throws EmployeeNotFoundException {
        Employee consumed = inputEmployeeService.produceKafkaEventEmployeeDelete(id);
        inputEmployeeService.deleteEmployee(consumed.getEmployeeId());
        return new ResponseEntity<>(String
                .format("<%s> to delete is sent to topic, %n <%s> is deleted from db", consumed, id),
                HttpStatus.OK);
    }
    @PutMapping(value = "/employees/{id}")
    public ResponseEntity<Object> update(@PathVariable(name = "id") String id, @RequestBody EmployeeDto dto) throws EmployeeTypeInvalidException, EmployeeEmptyFieldsException, EmployeeStateInvalidException, RemoteApiAddressNotLoadedException, EmployeeNotFoundException {
        Employee consumed = inputEmployeeService.produceKafkaEventEmployeeEdit(dto,id);
        Employee saved = inputEmployeeService.editEmployee(consumed);
        return new ResponseEntity<>(String
                .format("<%s> to update is sent and consumed;%n <%s> is updated in db",
                        consumed, saved),
                HttpStatus.OK);
    }
    private List<Employee> setAddressToEmployee(List<Employee> employees){
        employees.forEach((var employee)->{
            try {
                Address address = remoteInputAddressService.getRemoteAddressById(employee.getAddressId());
                employee.setAddress(address);
            } catch (RemoteApiAddressNotLoadedException e) {
              e.getMessage();
            }
        });
        return employees;
    }
}
