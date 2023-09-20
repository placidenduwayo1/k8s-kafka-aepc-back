package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface InputEmployeeService {
    Employee produceKafkaEventEmployeeCreate(EmployeeDto employeeDto) throws
            EmployeeTypeInvalidException, EmployeeEmptyFieldsException,
            EmployeeStateInvalidException, RemoteApiAddressNotLoadedException, EmployeeAlreadyExistsException;
    Employee createEmployee(Employee employee) throws
            EmployeeAlreadyExistsException, EmployeeEmptyFieldsException,
            EmployeeTypeInvalidException, EmployeeStateInvalidException,
            RemoteApiAddressNotLoadedException;
    Optional<Employee> getEmployeeById(String employeeId) throws EmployeeNotFoundException;
    List<Employee> loadEmployeeByInfo(String firstname, String lastname, String state, String type, String addressId);
    List<Employee> loadAllEmployees();
    Employee produceKafkaEventEmployeeDelete(String employeeId) throws EmployeeNotFoundException;
    String deleteEmployee(String employeeId) throws EmployeeNotFoundException;
    Employee produceKafkaEventEmployeeEdit(EmployeeDto payload, String employeeId) throws
            RemoteApiAddressNotLoadedException, EmployeeNotFoundException, EmployeeTypeInvalidException, EmployeeEmptyFieldsException, EmployeeStateInvalidException;
    Employee editEmployee(Employee payload);
    List<Employee> loadEmployeesByRemoteAddress(String addressId) throws RemoteApiAddressNotLoadedException;
}
