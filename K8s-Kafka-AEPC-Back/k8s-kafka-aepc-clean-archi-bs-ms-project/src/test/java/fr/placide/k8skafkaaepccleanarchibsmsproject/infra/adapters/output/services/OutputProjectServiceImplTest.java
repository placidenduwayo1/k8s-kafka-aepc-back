package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.CompanyServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.EmployeeServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
class OutputProjectServiceImplTest {
    @Mock
    private ProjectRepository repository;
    @Mock
    private EmployeeServiceProxy employeeServiceProxy;
    @Mock
    private CompanyServiceProxy companyServiceProxy;
    @InjectMocks
    private OutputProjectServiceImpl underTest;
    private static final String TOPIC1 = "topic1";
    private static final String TOPIC2 = "topic2";
    private static final String TOPIC3 = "topic3";
    private static final String PROJECT_ID = "project-id";
    private Project project;
    private static final String EMPLOYEE_ID = "employee-id";
    private Employee employee;
    private static final String COMPANY_ID = "company-id";
    private Company company;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employee = new Employee(EMPLOYEE_ID, "Placide", "Nduwayo", "placide.nduwayo@natan.fr", "2020-10-27:00:00:00",
                "active", "software-engineer");
        company = new Company(COMPANY_ID, "Natan", "Paris", "esn", "company-creation");
        project = new Project(PROJECT_ID, "Guppy", "outil d'aide au business analyst (BA) à la rédaction des us",
                1, "ongoing", Instant.now().toString(), EMPLOYEE_ID, employee, COMPANY_ID, company);
    }

    @Test
    void consumeKafkaEventProjectCreate() {
        //PREPARE
        //EXECUTE
        Project consumed = underTest.consumeKafkaEventProjectCreate(project, TOPIC1);
        log.info("{}", consumed);
        //VERIFY
        Assertions.assertNotNull(consumed);

    }

    @Test
    void saveProject() {
        //PREPARE
        ProjectModel model = Mapper.fromTo(project);
        //EXECUTE
        Mockito.when(repository.save(Mockito.any(ProjectModel.class))).thenReturn(model);
        Project saved = underTest.saveProject(project);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).save(model),
                () -> Assertions.assertNotNull(saved));
    }

    @Test
    void getProject() throws ProjectNotFoundException {
        //PREPARE
        ProjectModel pModel = Mapper.fromTo(project);
        //EXECUTE
        Mockito.when(repository.findById(PROJECT_ID)).thenReturn(Optional.of(pModel));
        Project pBean = underTest.getProject(PROJECT_ID).orElseThrow(ProjectNotFoundException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findById(PROJECT_ID),
                ()-> Assertions.assertNotNull(pBean));
    }

    @Test
    void loadProjectByInfo() {
        //PREPARE
        String name="Guppy";
        String description="description";
        String state ="ongoing";
        ProjectModel pModel = Mapper.fromTo(project);
        //EXECUTE
        Mockito.when(repository.findByNameAndDescriptionAndStateAndEmployeeIdAndCompanyId(name,description,state,EMPLOYEE_ID,COMPANY_ID))
                .thenReturn(List.of(pModel));
        List<Project> pBeans = underTest.loadProjectByInfo(name,description,state, EMPLOYEE_ID, COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findByNameAndDescriptionAndStateAndEmployeeIdAndCompanyId(name,description,state,EMPLOYEE_ID,COMPANY_ID),
                ()-> Assertions.assertEquals(1,pBeans.size()));
    }

    @Test
    void consumeKafkaEventProjectDelete() {
        //PREPARE
        //EXECUTE
        Project consumed = underTest.consumeKafkaEventProjectDelete(project, TOPIC2);
        //VERIFY
        Assertions.assertEquals(project, consumed);
    }

    @Test
    void deleteProject() throws ProjectNotFoundException {
        //PREPARE
        ProjectModel pModel = Mapper.fromTo(project);
        //EXECUTE
        Mockito.when(repository.findById(PROJECT_ID)).thenReturn(Optional.of(pModel));
        Project obtained = underTest.getProject(PROJECT_ID).orElseThrow(ProjectNotFoundException::new);
        Project consumed = underTest.consumeKafkaEventProjectDelete(obtained, TOPIC2);
        String msg = underTest.deleteProject(consumed.getProjectId());
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Assertions.assertNotNull(msg),
                () -> Assertions.assertEquals("project <" + consumed + "> is deleted", msg),
                () -> Mockito.verify(repository).deleteById(PROJECT_ID));
    }

    @Test
    void consumeKafkaEventProjectUpdate() {
        //PREPARE
        //EXECUTE
        Project consumed = underTest.consumeKafkaEventProjectUpdate(project, TOPIC3);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Assertions.assertNotNull(consumed));
    }

    @Test
    void updateProject() throws ProjectNotFoundException {
        //PREPARE
        ProjectModel pModel = Mapper.fromTo(project);
        //EXECUTE
        Mockito.when(repository.findById(PROJECT_ID)).thenReturn(Optional.of(pModel));
        Project obtained = underTest.getProject(PROJECT_ID).orElseThrow(ProjectNotFoundException::new);
        Project consumed = underTest.consumeKafkaEventProjectUpdate(project, TOPIC3);
        Mockito.when(repository.save(Mapper.fromTo(consumed))).thenReturn(pModel);
        Project pBean = underTest.updateProject(consumed);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findById(PROJECT_ID),
                () -> Mockito.verify(repository).save(Mapper.fromTo(consumed)),
                () -> Assertions.assertNotNull(pBean),
                () -> Assertions.assertNotNull(obtained));
    }

    @Test
    void loadProjectsAssignedToEmployee() {
        //PREPARE
        List<ProjectModel> pModels = List.of(Mapper.fromTo(project), Mapper.fromTo(project));
        //EXECUTE
        Mockito.when(repository.findByEmployeeId(EMPLOYEE_ID)).thenReturn(pModels);
        List<Project> pBeans = underTest.loadProjectsAssignedToEmployee(EMPLOYEE_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findByEmployeeId(EMPLOYEE_ID),
                () -> Assertions.assertEquals(2, pBeans.size()));
    }

    @Test
    void loadProjectsOfCompanyC() {
        //PREPARE
        List<ProjectModel> pModels = List.of(Mapper.fromTo(project), Mapper.fromTo(project));
        //EXECUTE
        Mockito.when(repository.findByCompanyId(COMPANY_ID)).thenReturn(pModels);
        List<Project> pBeans = underTest.loadProjectsOfCompanyC(COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findByCompanyId(COMPANY_ID),
                () -> Assertions.assertEquals(2, pBeans.size()));
    }

    @Test
    void getAllProjects() {
        //PREPARE
        List<ProjectModel> projectModels = List.of(Mapper.fromTo(project), Mapper.fromTo(project));
        //EXECUTE
        Mockito.when(repository.findAll()).thenReturn(projectModels);
        List<Project> projects = underTest.getAllProjects();
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(repository).findAll(),
                () -> Assertions.assertEquals(2, projects.size()));
    }

    @Test
    void getRemoteCompanyAPI() throws RemoteCompanyApiException {
        //PREPARE
        CompanyModel cModel = Mapper.fromTo(company);
        //EXECUTE
        Mockito.when(companyServiceProxy.loadRemoteApiGetCompany(COMPANY_ID)).thenReturn(Optional.of(cModel));
        Company cBean = underTest.getRemoteCompanyAPI(COMPANY_ID).orElseThrow(RemoteCompanyApiException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(companyServiceProxy).loadRemoteApiGetCompany(COMPANY_ID),
                () -> Assertions.assertNotNull(cBean));
    }

    @Test
    void getRemoteEmployeeAPI() throws RemoteEmployeeApiException {
        //PREPARE
        EmployeeModel eModel = Mapper.fromTo(employee);
        //EXECUTE
        Mockito.when(employeeServiceProxy.loadRemoteApiGetEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(eModel));
        Employee eBean = underTest.getRemoteEmployeeAPI(EMPLOYEE_ID).orElseThrow(RemoteEmployeeApiException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(employeeServiceProxy).loadRemoteApiGetEmployee(EMPLOYEE_ID),
                () -> Assertions.assertNotNull(eBean));
    }
}