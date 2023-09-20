package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyDto {
    private String id;
    private String name;
    private String agency;
    private String type;
}
