package fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;

import java.util.List;
import java.util.Optional;

public interface OutputCompanyService {
    Company consumeKafkaEventCompanyCreate(Company company, String topic);
    Company saveCompany(Company company);
    Optional<Company> getCompanyById(String id) throws CompanyNotFoundException;
    List<Company> loadCompanyByInfo(String name, String agency,String type);
    List<Company> loadAllCompanies();
    Company consumeKafkaEventCompanyDelete(Company company, String topic);
    String deleteCompany(String id) throws CompanyNotFoundException;
    Company consumeKafkaEventCompanyEdit(Company company, String topic);
    Company editCompany(Company company);
}
