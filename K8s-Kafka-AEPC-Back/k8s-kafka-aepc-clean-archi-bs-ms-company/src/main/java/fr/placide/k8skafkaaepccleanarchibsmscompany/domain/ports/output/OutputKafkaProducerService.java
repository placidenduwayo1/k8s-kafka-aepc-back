package fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;

public interface OutputKafkaProducerService {
    Company produceKafkaEventCompanyCreate(Company company);
    Company produceKafkaEventCompanyDelete(String id) throws CompanyNotFoundException;
    Company produceKafkaEventCompanyEdit(CompanyDto payload, String id) throws CompanyNotFoundException;
}
