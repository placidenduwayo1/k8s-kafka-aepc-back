package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.input.controller;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.input.InputCompanyService;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyAlreadyExistsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyEmptyFieldsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyTypeInvalidException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CompanyController {
    private final InputCompanyService companyService;

    public CompanyController(InputCompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping(value = "/companies")
    public ResponseEntity<Object> produceConsumeAndSaveCompany(@RequestBody CompanyDto dto) throws
            CompanyEmptyFieldsException, CompanyAlreadyExistsException, CompanyTypeInvalidException {

        Company consumed = companyService.produceKafkaEventCompanyCreate(dto);
        Company saved = companyService.createCompany(consumed);
        return new ResponseEntity<>(String
                .format("<%s> is sent and consumed;%n <%s> is saved in db", consumed, saved),
                HttpStatus.OK);
    }
    @GetMapping(value = "/companies")
    public List<Company> loadAllCompanies(){
       return companyService.loadAllCompanies();
    }
    @GetMapping(value = "/companies/{id}")
    public Optional<Company> loadCompany(@PathVariable(name = "id") String id) throws CompanyNotFoundException {
        return companyService.getCompanyById(id);
    }
    @PutMapping(value = "/companies/{id}")
    public ResponseEntity<Object> updateCompany(@RequestBody CompanyDto dto, @PathVariable(name = "id") String id) throws
            CompanyEmptyFieldsException, CompanyTypeInvalidException, CompanyNotFoundException {
        Company consumed = companyService.produceKafkaEventCompanyEdit(dto,id);
        Company saved = companyService.editCompany(consumed);
        return new ResponseEntity<>(String
                .format("<%s> to update is sent and consumed;%n <%s> is updated in db",
                        consumed, saved),
                HttpStatus.OK);
    }
    @DeleteMapping(value = "/companies/{id}")
    public ResponseEntity<Object> deleteCompany(@PathVariable(name = "id") String id) throws CompanyNotFoundException {
        Company consumed = companyService.produceKafkaEventCompanyDelete(id);
        companyService.deleteCompany(consumed.getCompanyId());
        return new ResponseEntity<>(String.format("<%s> to delete is sent to topic, %n <%s> is deleted from db",
                consumed, id), HttpStatus.OK);
    }
}
