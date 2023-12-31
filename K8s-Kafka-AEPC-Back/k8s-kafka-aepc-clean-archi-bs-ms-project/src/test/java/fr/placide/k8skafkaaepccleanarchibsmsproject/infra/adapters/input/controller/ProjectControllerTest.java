package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.controller;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.InputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;


class ProjectControllerTest {
    @Mock
    private InputProjectService inputProjectService;
    @Mock
    private RemoteInputEmployeeAPIService inputEmployeeService;
    @Mock
    private RemoteInputCompanyAPIService inputCompanyService;
    @InjectMocks
    private ProjectController underTest;
    private static final String PROJECT_ID = "project-id";
    private ProjectDto dto;
    private static final String EMPLOYEE_ID = "employee-id";
    private Employee employee;
    private static final String COMPANY_ID = "company-id";
    private Company company;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
       dto = ProjectDto.builder()
                .name("Guppy")
                .description("")
                .priority(1)
                .state("ongoing")
                .employeeId(EMPLOYEE_ID)
                .companyId(COMPANY_ID)
                .build();
        employee = new Employee(EMPLOYEE_ID, "Placide", "Nduwayo", "placide.nduwayo@natan.fr", "2020-10-27:00:00:00",
                "active", "software-engineer");
        company = new Company(COMPANY_ID, "Natan", "Paris", "esn", "company-creation");
    }

    @Test
    void produceConsumeAndSave() throws RemoteCompanyApiException, ProjectPriorityInvalidException, ProjectAlreadyExistsException,
            RemoteEmployeeApiException, ProjectStateInvalidException, ProjectFieldsEmptyException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        bean.setCompany(company);
        bean.setEmployee(employee);
        //EXECUTE
        Mockito.when(inputEmployeeService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Mockito.when(inputCompanyService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Mockito.when(inputProjectService.produceKafkaEventProjectCreate(Mockito.any(ProjectDto.class))).thenReturn(bean);
        ResponseEntity<Object> msg = underTest.produceConsumeAndSave(dto);
        Mockito.when(inputProjectService.createProject(Mockito.any(Project.class))).thenReturn(bean);

        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Mockito.verify(inputCompanyService, Mockito.atLeast(1)).getRemoteCompanyAPI(COMPANY_ID);
            Mockito.verify(inputEmployeeService, Mockito.atLeast(1)).getRemoteEmployeeAPI(EMPLOYEE_ID);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).produceKafkaEventProjectCreate(dto);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).createProject(bean);
            Assertions.assertNotNull(msg);
            Assertions.assertNotNull(bean.getCompany());
            Assertions.assertNotNull(bean.getEmployee());
        });
    }

    @Test
    void getAllProjects() {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto), Mapper.fromTo(dto));
        beans.forEach(bean -> {
            bean.setCompany(company);
            bean.setEmployee(employee);
        });
        //EXECUTE
        Mockito.when(inputProjectService.getAllProjects()).thenReturn(beans);
        List<Project> actuals = underTest.getAllProjects();
        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).getAllProjects();
            Assertions.assertEquals(beans, actuals);
            actuals.forEach((var project) -> {
                Assertions.assertNotNull(project.getCompany());
                Assertions.assertNotNull(project.getEmployee());
            });
        });
    }

    @Test
    void getProject() throws ProjectNotFoundException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        bean.setEmployee(employee);
        bean.setCompany(company);
        //EXECUTE
        Mockito.when(inputProjectService.getProject(PROJECT_ID)).thenReturn(Optional.of(bean));
        Project actual = underTest.getProject(PROJECT_ID);
        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).getProject(PROJECT_ID);
            Assertions.assertEquals("employee-id", actual.getEmployee().getEmployeeId());
            Assertions.assertNotNull(actual.getEmployee());
            Assertions.assertNotNull(actual.getCompany());
            Assertions.assertEquals(bean, actual);
        });
    }

    @Test
    void getProjectsByEmployee() throws RemoteEmployeeApiException {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto), Mapper.fromTo(dto));
        beans.forEach(bean -> {
            bean.setCompany(company);
            bean.setEmployee(employee);
        });
        //EXECUTE
        Mockito.when(inputProjectService.loadProjectsAssignedToEmployee(EMPLOYEE_ID)).thenReturn(beans);
        List<Project> actuals = underTest.getProjectsByEmployee(EMPLOYEE_ID);
        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Assertions.assertEquals(beans, actuals);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).loadProjectsAssignedToEmployee(EMPLOYEE_ID);
            actuals.forEach((var project) -> {
                Assertions.assertNotNull(project.getCompany());
                Assertions.assertNotNull(project.getEmployee());
            });
        });
    }

    @Test
    void getProjectsByCompany() throws RemoteCompanyApiException {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto), Mapper.fromTo(dto));
        beans.forEach(bean -> {
            bean.setCompany(company);
            bean.setEmployee(employee);
        });
        //EXECUTE
        Mockito.when(inputProjectService.loadProjectsOfCompanyC(COMPANY_ID)).thenReturn(beans);
        List<Project> actuals = underTest.getProjectsByCompany(COMPANY_ID);
        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Assertions.assertEquals(beans, actuals);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).loadProjectsOfCompanyC(COMPANY_ID);
            actuals.forEach((var project) -> {
                Assertions.assertNotNull(project.getCompany());
                Assertions.assertNotNull(project.getEmployee());
            });
        });
    }

    @Test
    void delete() throws ProjectNotFoundException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(inputProjectService.produceKafkaEventProjectDelete(PROJECT_ID)).thenReturn(bean);
        Mockito.when(inputProjectService.deleteProject(bean.getProjectId())).thenReturn("");
        ResponseEntity<Object> actual = underTest.delete(PROJECT_ID);
        //VERIFY
        Assertions.assertAll("assertions", () -> {
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).produceKafkaEventProjectDelete(PROJECT_ID);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).deleteProject(bean.getProjectId());
            Assertions.assertNotNull(actual);
        });
    }

    @Test
    void update() throws ProjectNotFoundException, RemoteCompanyApiException, ProjectPriorityInvalidException,
            RemoteEmployeeApiException, ProjectStateInvalidException, ProjectFieldsEmptyException {

        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(inputProjectService.getProject(PROJECT_ID)).thenReturn(Optional.of(bean));
        Project actual = underTest.getProject(PROJECT_ID);
        Mockito.when(inputEmployeeService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Mockito.when(inputCompanyService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Mockito.when(inputProjectService.produceKafkaEventProjectUpdate(dto,PROJECT_ID)).thenReturn(bean);
        Mockito.when(inputProjectService.updateProject(actual)).thenReturn(new Project());
        ResponseEntity<Object> msg = underTest.update(PROJECT_ID, dto);
        //VERIFY
        Assertions.assertAll("assertions",()->{
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).getProject(PROJECT_ID);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).produceKafkaEventProjectUpdate(dto, PROJECT_ID);
            Mockito.verify(inputProjectService, Mockito.atLeast(1)).updateProject(actual);
            Assertions.assertNotNull(msg);
        });
    }
}