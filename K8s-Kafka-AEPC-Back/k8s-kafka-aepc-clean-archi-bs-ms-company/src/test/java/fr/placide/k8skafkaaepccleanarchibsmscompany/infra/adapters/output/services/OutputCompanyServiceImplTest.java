package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.mapper.CompanyMapper;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.repository.CompanyRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
class OutputCompanyServiceImplTest {

    @Mock
    private CompanyRepository repository;
    @InjectMocks
    private OutputCompanyServiceImpl underTest;
    private Company company;
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";
    private static final String TOPIC3 = "topic3";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //PREPARE
        company = new Company(UUID.randomUUID().toString(), "natan", "paris", "esn",
                new Timestamp(System.currentTimeMillis()).toString());
    }

    @Test
    void consumeKafkaEventCompanyCreate() {
        //PREPARE

        //EXECUTE
        Company consumed = underTest.consumeKafkaEventCompanyCreate(company, TOPIC1);
        log.info("consumed company to create <{}> ", consumed);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertNotNull(consumed),
                () -> Assertions.assertEquals(consumed, company)
        );
    }

    @Test
    void saveCompany() {
        //PREPARE
        Company consumed = underTest.consumeKafkaEventCompanyCreate(company, TOPIC1);
        CompanyModel model = CompanyMapper.fromBeanToModel(consumed);
        CompanyModel expectedModel = new CompanyModel(UUID.randomUUID().toString(), "natan", "lille", "esn",
                new Timestamp(System.currentTimeMillis()).toString());
        //EXECUTE
        Mockito.when(repository.save(model)).thenReturn(expectedModel);
        Company saved = underTest.saveCompany(consumed);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertNotNull(saved),
                () -> Assertions.assertNotEquals(saved, consumed),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).save(model));
    }

    @Test
    void getCompanyById() throws CompanyNotFoundException {
        //PREPARE
        String id = "uuid";
        CompanyModel model = CompanyMapper.fromBeanToModel(company);
        //EXECUTE
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(model));
        Company obtained = underTest.getCompanyById(id).orElseThrow(CompanyNotFoundException::new);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertNotNull(obtained),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).findById(id));
        log.info("{}", obtained);
    }

    @Test
    void loadCompanyByInfo() {
        //PREPARE
        List<Company> companies = List.of(company);
        String name = "natan";
        String agency = "paris";
        String type = "esn";
        List<CompanyModel> models = companies.stream().map(CompanyMapper::fromBeanToModel).toList();
        //EXECUTE
        Mockito.when(repository.findByNameAndAgencyAndType(name, agency, type)).thenReturn(models);
        List<Company> obtainedCompanies = underTest.loadCompanyByInfo(name, agency, type);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertEquals(1, obtainedCompanies.size()),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).findByNameAndAgencyAndType(name, agency, type),
                () -> Assertions.assertFalse(obtainedCompanies.contains(company))

        );
    }

    @Test
    void loadAllCompanies() {
        //PREPARE
        List<Company> companies = List.of(company);
        List<CompanyModel> models = companies.stream().map(CompanyMapper::fromBeanToModel).toList();
        //EXECUTE
        Mockito.when(repository.findAll()).thenReturn(models);
        List<Company> obtained = underTest.loadAllCompanies();
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Assertions.assertEquals(1, obtained.size()),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).findAll());
    }

    @Test
    void consumeKafkaEventCompanyDelete() {
        //PREPARE

        //EXECUTE
        Company consumed = underTest.consumeKafkaEventCompanyDelete(company, TOPIC2);
        //VERIFY
        Assertions.assertNotNull(consumed);
    }

    @Test
    void deleteCompany() throws CompanyNotFoundException {
        //PREPARE
        String id = "uuid";
        CompanyModel model = CompanyMapper.fromBeanToModel(company);
        //EXECUTE
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(model));
        Company obtained = underTest.getCompanyById(id).orElseThrow(CompanyNotFoundException::new);
        Company consumed = underTest.consumeKafkaEventCompanyDelete(obtained, TOPIC2);
        Mockito.when(repository.findById(consumed.getCompanyId())).thenReturn(Optional.of(model));
        String expectedMsg = underTest.deleteCompany(consumed.getCompanyId());
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Mockito.verify(repository, Mockito.atLeast(1)).deleteById(consumed.getCompanyId()),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).findById(id),
                () -> Assertions.assertEquals("company <" + consumed + "> deleted", expectedMsg)
        );
    }

    @Test
    void consumeKafkaEventCompanyEdit() {
        //PREPARE

        //EXECUTE
        Company consumed = underTest.consumeKafkaEventCompanyEdit(company, TOPIC3);
        //VERIFY
        Assertions.assertNotNull(consumed);
    }

    @Test
    void editCompany() throws CompanyNotFoundException {
        //PREPARE
        String id = "uuid";
        Company updated = new Company(UUID.randomUUID().toString(), "natan", "paris", "client",
                new Timestamp(System.currentTimeMillis()).toString());
        CompanyModel model = CompanyMapper.fromBeanToModel(updated);
        //EXECUTE
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(model));
        Company obtained = underTest.getCompanyById(id).orElseThrow(CompanyNotFoundException::new);
        Mockito.when(repository.findById(obtained.getCompanyId())).thenReturn(Optional.of(model));
        Company consumed = underTest.consumeKafkaEventCompanyEdit(obtained, TOPIC3);
        CompanyModel mapped = CompanyMapper.fromBeanToModel(consumed);
        Mockito.when(repository.save(mapped)).thenReturn(model);
        Company saved = underTest.editCompany(consumed);
        //VERIFY
        Assertions.assertAll("grp of assertions",
                () -> Mockito.verify(repository, Mockito.atLeast(1)).save(mapped),
                () -> Mockito.verify(repository, Mockito.atLeast(1)).findById(id),
                () -> Assertions.assertNotNull(saved),
                () -> Assertions.assertNotNull(consumed));

    }
}