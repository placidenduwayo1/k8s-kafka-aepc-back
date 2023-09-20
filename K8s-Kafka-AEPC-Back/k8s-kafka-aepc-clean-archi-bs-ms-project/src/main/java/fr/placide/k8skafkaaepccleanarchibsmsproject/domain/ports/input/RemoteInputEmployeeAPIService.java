package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;

import java.util.Optional;


public interface RemoteInputEmployeeAPIService {
    Optional<Employee> getRemoteEmployeeAPI(String employeeId) throws RemoteEmployeeApiException;
}
