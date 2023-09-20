package fr.placide.k8skafkaaepccleanarchibsmscompany.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyAlreadyExistsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyEmptyFieldsException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyTypeInvalidException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output.OutputCompanyService;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output.OutputKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.mapper.CompanyMapper;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

class UseCaseTest {
    @Mock
    private OutputKafkaProducerService kafkaProducerService;
    @Mock
    private OutputCompanyService companyService;
    @InjectMocks
    private UseCase underTest;
    private static final String COMPANY_ID="company-id";
    private static final String NAME="Natan";
    private static final String AGENCY="Paris";
    private static final String TYPE ="esn";
    private CompanyDto dto;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dto = CompanyDto.builder()
                .name(NAME)
                .agency(AGENCY)
                .type(TYPE)
                .build();
    }

    @Test
    void produceKafkaEventCompanyCreate() throws CompanyEmptyFieldsException, CompanyAlreadyExistsException, CompanyTypeInvalidException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(kafkaProducerService.produceKafkaEventCompanyCreate(Mockito.any(Company.class))).thenReturn(bean);
        Company actual = underTest.produceKafkaEventCompanyCreate(dto);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventCompanyCreate(Mockito.any(Company.class));
            Assertions.assertEquals(bean, actual);
        });
    }

    @Test
    void createCompany() {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(companyService.saveCompany(Mockito.any(Company.class))).thenReturn(bean);
        Company actual = underTest.createCompany(bean);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(companyService, Mockito.atLeast(1)).saveCompany(bean);
            Assertions.assertEquals(bean, actual);
        });
    }

    @Test
    void getCompanyById() throws CompanyNotFoundException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(companyService.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(bean));
        Company actual = underTest.getCompanyById(COMPANY_ID).orElseThrow(CompanyNotFoundException::new);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(companyService, Mockito.atLeast(1)).getCompanyById(COMPANY_ID);
            Assertions.assertEquals(bean, actual);
        });
    }

    @Test
    void loadCompanyByInfo() {
        //PREPARE
        List<Company> beans = List.of(CompanyMapper.fromDtoToBean(dto));
        //EXECUTE
        Mockito.when(companyService.loadCompanyByInfo(NAME,AGENCY,TYPE)).thenReturn(beans);
        List<Company> actuals = underTest.loadCompanyByInfo(NAME,AGENCY,TYPE);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(companyService, Mockito.atLeast(1)).loadCompanyByInfo(NAME,AGENCY,TYPE);
            Assertions.assertEquals(1, actuals.size());
        });
    }

    @Test
    void loadAllCompanies() {
        //PREPARE
        List<Company> beans = List.of(CompanyMapper.fromDtoToBean(dto));
        //EXECUTE
        Mockito.when(companyService.loadAllCompanies()).thenReturn(beans);
        List<Company> actuals = underTest.loadAllCompanies();
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(companyService, Mockito.atLeast(1)).loadAllCompanies();
            Assertions.assertEquals(1, actuals.size());
        });
    }

    @Test
    void produceKafkaEventCompanyDelete() throws CompanyNotFoundException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(companyService.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(bean));
        Company actual1 = underTest.getCompanyById(COMPANY_ID).orElseThrow(CompanyNotFoundException::new);
        Mockito.when(kafkaProducerService.produceKafkaEventCompanyDelete(COMPANY_ID)).thenReturn(bean);
        Company actual2 = underTest.produceKafkaEventCompanyDelete(COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",()->{
           Mockito.verify(companyService,Mockito.atLeast(1)).getCompanyById(COMPANY_ID);
           Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventCompanyDelete(COMPANY_ID);
           Assertions.assertEquals(actual1,actual2);
        });
    }

    @Test
    void deleteCompany() throws CompanyNotFoundException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(companyService.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(bean));
        Company actual = underTest.getCompanyById(COMPANY_ID).orElseThrow(CompanyNotFoundException::new);
        Mockito.when(companyService.deleteCompany(actual.getCompanyId())).thenReturn("");
        String msg = underTest.deleteCompany(COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",()->{
            Mockito.verify(companyService,Mockito.atLeast(1)).getCompanyById(COMPANY_ID);
            Mockito.verify(companyService, Mockito.atLeast(1)).deleteCompany(actual.getCompanyId());
            Assertions.assertEquals("Company <"+actual+"> successfully deleted",msg);
        });
    }

    @Test
    void produceKafkaEventCompanyEdit() throws CompanyNotFoundException, CompanyEmptyFieldsException, CompanyTypeInvalidException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(kafkaProducerService.produceKafkaEventCompanyEdit(dto,COMPANY_ID)).thenReturn(bean);
        Company actual = underTest.produceKafkaEventCompanyEdit(dto,COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",()->{
            Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventCompanyEdit(dto,COMPANY_ID);
            Assertions.assertEquals(bean,actual);
        });
    }

    @Test
    void editCompany() throws CompanyNotFoundException {
        //PREPARE
        Company bean = CompanyMapper.fromDtoToBean(dto);
        //EXECUTE
        Mockito.when(companyService.getCompanyById(COMPANY_ID)).thenReturn(Optional.of(bean));
        Company actual1 = underTest.getCompanyById(COMPANY_ID).orElseThrow(CompanyNotFoundException::new);
        Mockito.when(companyService.editCompany(bean)).thenReturn(bean);
        Company actual2 = underTest.editCompany(bean);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(companyService, Mockito.atLeast(1)).getCompanyById(COMPANY_ID);
            Mockito.verify(companyService, Mockito.atLeast(1)).editCompany(bean);
            Assertions.assertEquals(bean, actual2);
            Assertions.assertEquals(actual1, actual2);
        });
    }
}