package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.EmployeeNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;

public interface OutputEmployeeServiceKafkaProducer {
    Employee produceKafkaEventEmployeeCreate(Employee employee);
    Employee produceKafkaEventEmployeeDelete(String employeeId) throws EmployeeNotFoundException;
    Employee produceKafkaEventEmployeeEdit(EmployeeDto employeeDto, String employeeId) throws
            EmployeeNotFoundException, RemoteApiAddressNotLoadedException;
}
