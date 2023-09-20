package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.fallback;

import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.EmployeeServiceProxy;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.Msg.REMOTE_EMPLOYEE_API_UNREACHABLE;

@Component
public class EmployeeServiceFallback implements EmployeeServiceProxy {
    @Override
    public Optional<EmployeeModel> loadRemoteApiGetEmployee(String employeeId) {
        return Optional.of( EmployeeModel.builder()
                .employeeId(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .firstname(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .lastname(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .email(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .hireDate(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .state(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .type(REMOTE_EMPLOYEE_API_UNREACHABLE)
                .build());
    }
}
