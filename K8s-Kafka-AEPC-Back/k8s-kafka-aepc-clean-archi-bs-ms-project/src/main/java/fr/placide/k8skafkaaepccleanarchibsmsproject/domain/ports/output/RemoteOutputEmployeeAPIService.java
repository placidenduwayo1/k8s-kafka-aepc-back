package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;

import java.util.Optional;


public interface RemoteOutputEmployeeAPIService {
    Optional<Employee> getRemoteEmployeeAPI(String employeeId) throws RemoteEmployeeApiException;
}
