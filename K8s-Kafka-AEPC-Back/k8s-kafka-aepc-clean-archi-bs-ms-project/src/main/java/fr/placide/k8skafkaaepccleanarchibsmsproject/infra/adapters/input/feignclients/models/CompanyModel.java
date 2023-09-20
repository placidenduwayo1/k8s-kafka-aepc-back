package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyModel {
    private String companyId;
    private String name;
    private String agency;
    private String type;
    private String connectedDate;
}
