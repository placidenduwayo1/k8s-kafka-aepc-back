package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.controller;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.InputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final InputProjectService inputProjectService;
    private final RemoteInputEmployeeAPIService inputEmployeeService;
    private final RemoteInputCompanyAPIService inputCompanyService;

    public ProjectController(InputProjectService inputProjectService, RemoteInputEmployeeAPIService inputEmployeeService,
                             RemoteInputCompanyAPIService inputCompanyService) {
        this.inputProjectService = inputProjectService;
        this.inputEmployeeService = inputEmployeeService;
        this.inputCompanyService = inputCompanyService;
    }
    @PostMapping(value = "/projects")
    public ResponseEntity<Object> produceConsumeAndSave(@RequestBody ProjectDto dto) throws RemoteCompanyApiException,
            ProjectPriorityInvalidException, ProjectAlreadyExistsException, RemoteEmployeeApiException, ProjectStateInvalidException,
            ProjectFieldsEmptyException {
        Project consumed = inputProjectService.produceKafkaEventProjectCreate(dto);
        Project saved = inputProjectService.createProject(consumed);
        Employee remoteEmployee = inputEmployeeService.getRemoteEmployeeAPI(consumed.getEmployeeId())
                .orElseThrow(RemoteEmployeeApiException::new);
        Company remoteCompany = inputCompanyService.getRemoteCompanyAPI(consumed.getCompanyId())
                .orElseThrow(RemoteCompanyApiException::new);
        consumed.setEmployee(remoteEmployee);
        consumed.setCompany(remoteCompany);

        return new ResponseEntity<>(String
                .format("%s is sent and consumed;%n %s is saved in db", consumed, saved),
                HttpStatus.OK);
    }
    @GetMapping(value = "/projects")
    public List<Project> getAllProjects(){
        return setProjectDependency(inputProjectService.getAllProjects());
    }
    @GetMapping(value = "/projects/{id}")
    public Project getProject(@PathVariable(name = "id") String id) throws ProjectNotFoundException {
        return inputProjectService.getProject(id).orElseThrow(ProjectNotFoundException::new);
    }
    @GetMapping(value = "/projects/employees/{employeeId}")
    public List<Project> getProjectsByEmployee(@PathVariable(name = "employeeId") String employeeId) throws RemoteEmployeeApiException {
        List<Project> projects = inputProjectService.loadProjectsAssignedToEmployee(employeeId);
        return setProjectDependency(projects);
    }
    @GetMapping(value = "/projects/companies/{companyId}")
    public List<Project> getProjectsByCompany(@PathVariable(name = "companyId") String companyId) throws RemoteCompanyApiException {
        List<Project> projects = inputProjectService.loadProjectsOfCompanyC(companyId);
        return setProjectDependency(projects);
    }
    @DeleteMapping(value = "/projects/{id}")
    public ResponseEntity<Object> delete(@PathVariable(name = "id") String id) throws ProjectNotFoundException {
        Project consumed = inputProjectService.produceKafkaEventProjectDelete(id);
        inputProjectService.deleteProject(consumed.getProjectId());
        return new ResponseEntity<>(String
                .format("<%s> to delete is sent to topic, %n <%s> is deleted from db", consumed, id),
                HttpStatus.OK);
    }
    @PutMapping(value = "/projects/{id}")
    public ResponseEntity<Object> update(@PathVariable(name = "id") String id, @RequestBody ProjectDto dto) throws ProjectNotFoundException,
            RemoteCompanyApiException, ProjectPriorityInvalidException, RemoteEmployeeApiException, ProjectStateInvalidException, ProjectFieldsEmptyException {
        Project consumed = inputProjectService.produceKafkaEventProjectUpdate(dto, id);
        Project saved = inputProjectService.updateProject(consumed);
        return new ResponseEntity<>(String
                .format("<%s> to update is sent and consumed;%n <%s> is updated in db",
                        consumed, saved),
                HttpStatus.OK);
    }
    private List<Project> setProjectDependency(List<Project> projects){
        projects.forEach((var project)->{
            try {
                Employee remoteEmployee = inputEmployeeService.getRemoteEmployeeAPI(project.getEmployeeId())
                        .orElseThrow(RemoteEmployeeApiException::new);
                Company remoteCompany = inputCompanyService.getRemoteCompanyAPI(project.getCompanyId())
                        .orElseThrow(RemoteCompanyApiException::new);
                project.setEmployee(remoteEmployee);
                project.setCompany(remoteCompany);
            } catch (RemoteEmployeeApiException | RemoteCompanyApiException e) {
                e.getMessage();
            }
        });
        return projects;
    }
}
