package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;

import java.util.List;
import java.util.Optional;

public interface OutputEmployeeService {
    Employee consumeKafkaEventEmployeeCreate(Employee employee, String topic);
    Employee saveEmployee(Employee employee);
    Optional<Employee> getEmployeeById(String employeeId) throws EmployeeNotFoundException;
    List<Employee> loadEmployeesByRemoteAddress(String addressId) throws RemoteApiAddressNotLoadedException;
    List<Employee> loadEmployeeByInfo(String firstname, String lastname, String state, String type, String addressId);
    List<Employee> loadAllEmployees();
    Employee consumeKafkaEventEmployeeDelete(Employee employee, String topic);
    String deleteEmployee(String employeeId) throws EmployeeNotFoundException;

    Employee consumeKafkaEventEmployeeEdit(Employee employee, String topic);
    Employee editEmployee(Employee employee);
}
