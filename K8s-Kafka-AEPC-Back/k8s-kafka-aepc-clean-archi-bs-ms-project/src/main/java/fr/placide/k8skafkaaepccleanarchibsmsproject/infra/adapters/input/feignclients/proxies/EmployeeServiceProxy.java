package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies;

import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.fallback.EmployeeServiceFallback;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "k8s-kafka-aepc-bs-ms-employee", fallback = EmployeeServiceFallback.class)
@Qualifier(value = "employee-service-proxy")
public interface EmployeeServiceProxy {
    @GetMapping("/employees/{employeeId}")
    Optional<EmployeeModel> loadRemoteApiGetEmployee(@PathVariable(name = "employeeId") String employeeId);
}
