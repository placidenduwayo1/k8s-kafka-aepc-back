package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

@Slf4j
class UseCaseTest {
    @Mock
    private OutputProjectKafkaProducerService kafkaProducerService;
    @Mock
    private OutputProjectService outputProjectService;
    @Mock
    private RemoteOutputEmployeeAPIService outputEmployeeAPIService;
    @Mock
    private RemoteOutputCompanyAPIService outputCompanyAPIService;
    @InjectMocks
    private UseCase underTest;
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
                .description("outil d'aide au business analyst (BA) à la rédaction des us")
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
    void produceKafkaEventProjectCreate() throws RemoteCompanyApiException, ProjectPriorityInvalidException, ProjectAlreadyExistsException, RemoteEmployeeApiException, ProjectStateInvalidException, ProjectFieldsEmptyException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(kafkaProducerService.produceKafkaEventProjectCreate(Mockito.any(Project.class))).thenReturn(bean);
        Mockito.when(outputEmployeeAPIService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Mockito.when(outputCompanyAPIService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Project produced = underTest.produceKafkaEventProjectCreate(dto);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                ()->Mockito.verify(outputEmployeeAPIService,Mockito.atLeast(1)).getRemoteEmployeeAPI(EMPLOYEE_ID),
                ()->Mockito.verify(outputCompanyAPIService, Mockito.atLeast(1)).getRemoteCompanyAPI(COMPANY_ID),
                () -> Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventProjectCreate(Mockito.any()),
                () -> Assertions.assertNotNull(produced));
    }

    @Test
    void createProject() {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(outputProjectService.saveProject(Mockito.any(Project.class))).thenReturn(bean);
        Project saved = underTest.createProject(bean);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService,Mockito.atLeast(1)).saveProject(bean),
                () -> Assertions.assertEquals(bean, saved));
    }

    @Test
    void getProject() throws ProjectNotFoundException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(outputProjectService.getProject(PROJECT_ID)).thenReturn(Optional.of(bean));
        Project obtained = underTest.getProject(PROJECT_ID).orElseThrow(ProjectNotFoundException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService,Mockito.atLeast(1)).getProject(PROJECT_ID),
                () -> Assertions.assertEquals(bean, obtained));
    }

    @Test
    void loadProjectByInfo() {
        //PREPARE
        String name = "Guppy";
        String description = "description";
        String state = "ongoing";
        List<Project> projects = List.of(Mapper.fromTo(dto));
        //EXECUTE
        Mockito.when(outputProjectService.loadProjectByInfo(name, description, state, EMPLOYEE_ID, COMPANY_ID)).thenReturn(projects);
        List<Project> lists = underTest.loadProjectByInfo(name, description, state, EMPLOYEE_ID, COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).loadProjectByInfo(name, description, state, EMPLOYEE_ID, COMPANY_ID),
                () -> Assertions.assertEquals(projects.size(), lists.size()));
    }

    @Test
    void produceKafkaEventProjectDelete() throws ProjectNotFoundException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(kafkaProducerService.produceKafkaEventProjectDelete(PROJECT_ID)).thenReturn(bean);
        Project produced = underTest.produceKafkaEventProjectDelete(PROJECT_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventProjectDelete(PROJECT_ID),
                () -> Assertions.assertEquals(bean, produced));
    }

    @Test
    void deleteProject() throws ProjectNotFoundException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(outputProjectService.getProject(PROJECT_ID)).thenReturn(Optional.of(bean));
        Project obtained = underTest.getProject(PROJECT_ID).orElseThrow(ProjectNotFoundException::new);
        Mockito.when(outputProjectService.deleteProject(obtained.getProjectId())).thenReturn("");
        String msg = underTest.deleteProject(PROJECT_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).deleteProject(obtained.getProjectId()),
                () -> Assertions.assertEquals("Project" + obtained + "successfully deleted", msg),
                () -> Assertions.assertEquals(bean, obtained));
    }


    @Test
    void produceKafkaEventProjectUpdate() throws ProjectNotFoundException, RemoteCompanyApiException, RemoteEmployeeApiException,
            ProjectPriorityInvalidException, ProjectStateInvalidException, ProjectFieldsEmptyException {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(outputEmployeeAPIService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Mockito.when(outputCompanyAPIService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Mockito.when(kafkaProducerService.produceKafkaEventProjectEdit(dto, PROJECT_ID)).thenReturn(bean);
        Project produced = underTest.produceKafkaEventProjectUpdate(dto, PROJECT_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputEmployeeAPIService, Mockito.atLeast(1)).getRemoteEmployeeAPI(EMPLOYEE_ID),
                () -> Mockito.verify(outputCompanyAPIService, Mockito.atLeast(1)).getRemoteCompanyAPI(COMPANY_ID),
                () -> Mockito.verify(kafkaProducerService, Mockito.atLeast(1)).produceKafkaEventProjectEdit(dto, PROJECT_ID),
                () -> Assertions.assertNotNull(produced));
    }

    @Test
    void updateProject() {
        //PREPARE
        Project bean = Mapper.fromTo(dto);
        //EXECUTE
        Mockito.when(outputProjectService.updateProject(bean)).thenReturn(new Project());
        Project updated = underTest.updateProject(bean);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).updateProject(bean),
                () -> Assertions.assertNotNull(updated),
                () -> Assertions.assertNotEquals(dto.getDescription(), updated.getDescription()));
    }

    @Test
    void loadProjectsAssignedToEmployee() throws RemoteEmployeeApiException {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto));
        //EXECUTE
        Mockito.when(outputEmployeeAPIService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Employee obtained = underTest.getRemoteEmployeeAPI(EMPLOYEE_ID).orElseThrow(RemoteEmployeeApiException::new);
        Mockito.when(outputProjectService.loadProjectsAssignedToEmployee(EMPLOYEE_ID)).thenReturn(beans);
        List<Project> projects = underTest.loadProjectsAssignedToEmployee(EMPLOYEE_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputEmployeeAPIService, Mockito.atLeast(1)).getRemoteEmployeeAPI(EMPLOYEE_ID),
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).loadProjectsAssignedToEmployee(EMPLOYEE_ID),
                () -> Assertions.assertEquals(beans.get(0), projects.get(0)),
                () -> Assertions.assertEquals(beans.size(), projects.size()),
                () -> Assertions.assertEquals(employee, obtained));
    }

    @Test
    void loadProjectsOfCompanyC() throws RemoteCompanyApiException {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto));
        //EXECUTE
        Mockito.when(outputCompanyAPIService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Company actual = underTest.getRemoteCompanyAPI(COMPANY_ID).orElseThrow(RemoteCompanyApiException::new);
        Mockito.when(outputProjectService.loadProjectsOfCompanyC(actual.getCompanyId())).thenReturn(beans);
        List<Project> actuals = underTest.loadProjectsOfCompanyC(COMPANY_ID);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputCompanyAPIService, Mockito.atLeast(1)).getRemoteCompanyAPI(COMPANY_ID),
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).loadProjectsOfCompanyC(actual.getCompanyId()),
                () -> Assertions.assertEquals(company, actual),
                () -> Assertions.assertFalse(actuals.isEmpty()));
    }

    @Test
    void getAllProjects() {
        //PREPARE
        List<Project> beans = List.of(Mapper.fromTo(dto));
        //EXECUTE
        Mockito.when(outputProjectService.getAllProjects()).thenReturn(beans);
        List<Project> actuals = underTest.getAllProjects();
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputProjectService, Mockito.atLeast(1)).getAllProjects(),
                () -> Assertions.assertEquals(beans.size(), actuals.size()));
    }

    @Test
    void getRemoteCompanyAPI() throws RemoteCompanyApiException {
        //PREPARE
        //EXECUTE
        Mockito.when(outputCompanyAPIService.getRemoteCompanyAPI(COMPANY_ID)).thenReturn(Optional.of(company));
        Company actual = underTest.getRemoteCompanyAPI(COMPANY_ID).orElseThrow(RemoteCompanyApiException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputCompanyAPIService, Mockito.atLeast(1)).getRemoteCompanyAPI(COMPANY_ID),
                () -> Assertions.assertEquals(company, actual));
    }

    @Test
    void getRemoteEmployeeAPI() throws RemoteEmployeeApiException {
        //PREPARE
        //EXECUTE
        Mockito.when(outputEmployeeAPIService.getRemoteEmployeeAPI(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        Employee actual = underTest.getRemoteEmployeeAPI(EMPLOYEE_ID).orElseThrow(RemoteEmployeeApiException::new);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputEmployeeAPIService, Mockito.atLeast(1)).getRemoteEmployeeAPI(EMPLOYEE_ID),
                () -> Assertions.assertEquals(employee, actual));
    }
}