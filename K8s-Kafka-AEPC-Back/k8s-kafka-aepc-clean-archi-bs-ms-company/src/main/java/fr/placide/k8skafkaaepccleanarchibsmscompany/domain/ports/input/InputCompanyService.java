package fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyAlreadyExistsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyEmptyFieldsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyTypeInvalidException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;

import java.util.List;
import java.util.Optional;

public interface InputCompanyService {
    Company produceKafkaEventCompanyCreate(CompanyDto dto) throws
            CompanyTypeInvalidException, CompanyEmptyFieldsException, CompanyAlreadyExistsException;
    Company createCompany(Company company) throws
            CompanyAlreadyExistsException, CompanyEmptyFieldsException,
            CompanyTypeInvalidException;
    Optional<Company> getCompanyById(String id) throws CompanyNotFoundException;
    List<Company> loadCompanyByInfo(String name, String agency, String type);
    List<Company> loadAllCompanies();
    Company produceKafkaEventCompanyDelete(String id) throws CompanyNotFoundException;
    String deleteCompany(String id) throws CompanyNotFoundException;
    Company produceKafkaEventCompanyEdit(CompanyDto payload, String id) throws
            CompanyNotFoundException, CompanyTypeInvalidException, CompanyEmptyFieldsException;
    Company editCompany(Company payload);
}
