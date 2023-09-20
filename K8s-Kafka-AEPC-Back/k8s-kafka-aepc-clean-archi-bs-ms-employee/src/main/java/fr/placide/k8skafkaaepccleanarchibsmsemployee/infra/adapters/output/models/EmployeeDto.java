package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private String firstname;
    private String lastname;
    private String state;
    private String type;
    private String addressId;
}
