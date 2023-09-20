package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.InputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input.RemoteInputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UseCase implements InputProjectService, RemoteInputEmployeeAPIService, RemoteInputCompanyAPIService {
    private final OutputProjectKafkaProducerService kafkaProducerService;
    private final OutputProjectService outputProjectService;
    private final RemoteOutputEmployeeAPIService outputEmployeeAPIService;
    private final RemoteOutputCompanyAPIService outputCompanyAPIService;

    public UseCase(OutputProjectKafkaProducerService kafkaProducerService, OutputProjectService outputProjectService, RemoteOutputEmployeeAPIService outputEmployeeAPIService, RemoteOutputCompanyAPIService outputCompanyAPIService) {
        this.kafkaProducerService = kafkaProducerService;
        this.outputProjectService = outputProjectService;
        this.outputEmployeeAPIService = outputEmployeeAPIService;
        this.outputCompanyAPIService = outputCompanyAPIService;
    }

    private void checkProjectValidity(String name, String desc, int priority, String state, String employeeId, String companyId) throws
            ProjectFieldsEmptyException, ProjectPriorityInvalidException, ProjectStateInvalidException, RemoteEmployeeApiException, RemoteCompanyApiException {
        if(!Validator.isValidProject(name,desc,employeeId,companyId)){
            throw  new ProjectFieldsEmptyException();
        }
        else if(!Validator.isValidProject(priority)){
            throw new ProjectPriorityInvalidException();
        }
        else if (!Validator.isValidProject(state)){
            throw new ProjectStateInvalidException();
        }
        Employee employee = getRemoteEmployeeAPI(employeeId).orElseThrow(RemoteEmployeeApiException::new);
        if(Validator.remoteEmployeeApiUnreachable(employee.getEmployeeId())){
            throw new RemoteEmployeeApiException();
        }
        Company company = getRemoteCompanyAPI(companyId).orElseThrow(RemoteCompanyApiException::new);
        if(Validator.remoteCompanyApiUnreachable(company.getCompanyId())){
            throw new RemoteCompanyApiException();
        }
    }
    private void checkProjectAlreadyExists(String name, String desc, String state,String employeeId, String companyId) throws
            ProjectAlreadyExistsException {
        if(!loadProjectByInfo(name,desc,state,employeeId, companyId).isEmpty()){
            throw new ProjectAlreadyExistsException();
        }
    }
    @Override
    public Project produceKafkaEventProjectCreate(ProjectDto dto) throws ProjectAlreadyExistsException,
            ProjectPriorityInvalidException, ProjectStateInvalidException, RemoteEmployeeApiException, RemoteCompanyApiException,
            ProjectFieldsEmptyException {
        Validator.format(dto);
        checkProjectValidity(dto.getName(), dto.getDescription(), dto.getPriority(), dto.getState(), dto.getEmployeeId(),
                dto.getCompanyId());
        checkProjectAlreadyExists(dto.getName(),dto.getDescription(), dto.getState(),dto.getEmployeeId(), dto.getCompanyId());
        Project bean = Mapper.fromTo(dto);
        bean.setProjectId(UUID.randomUUID().toString());
        bean.setCreatedDate(Timestamp.from(Instant.now()).toString());
        Employee employee = getRemoteEmployeeAPI(dto.getEmployeeId()).orElseThrow(RemoteEmployeeApiException::new);
        Company company = getRemoteCompanyAPI(dto.getCompanyId()).orElseThrow(RemoteCompanyApiException::new);
        bean.setEmployee(employee);
        bean.setCompany(company);
        return kafkaProducerService.produceKafkaEventProjectCreate(bean);
    }

    @Override
    public Project createProject(Project project){
        return outputProjectService.saveProject(project);
    }

    @Override
    public Optional<Project> getProject(String projectId) throws ProjectNotFoundException {
        return Optional.of(outputProjectService.getProject(projectId).orElseThrow(ProjectNotFoundException::new));
    }

    @Override
    public List<Project> loadProjectByInfo(String name, String desc, String state, String employeeId, String companyId) {
        return outputProjectService.loadProjectByInfo(name,desc,state,employeeId,companyId);
    }

    @Override
    public Project produceKafkaEventProjectDelete(String projectId) throws ProjectNotFoundException {
        return kafkaProducerService.produceKafkaEventProjectDelete(projectId);
    }

    @Override
    public String deleteProject(String projectId) throws ProjectNotFoundException {
        Project project = getProject(projectId).orElseThrow(ProjectNotFoundException::new);
        outputProjectService.deleteProject(project.getProjectId());
        return "Project"+project+"successfully deleted";
    }

    @Override
    public Project produceKafkaEventProjectUpdate(ProjectDto payload, String projectId) throws ProjectNotFoundException,
            ProjectPriorityInvalidException, ProjectStateInvalidException, RemoteEmployeeApiException, RemoteCompanyApiException,
            ProjectFieldsEmptyException {
        Validator.format(payload);
        checkProjectValidity(payload.getName(), payload.getDescription(), payload.getPriority(), payload.getState(),
                payload.getEmployeeId(), payload.getCompanyId());
        return kafkaProducerService.produceKafkaEventProjectEdit(payload,projectId);
    }

    @Override
    public Project updateProject(Project payload) {
        return outputProjectService.updateProject(payload);
    }

    @Override
    public List<Project> loadProjectsAssignedToEmployee(String employeeId) throws RemoteEmployeeApiException {
        Employee employee = getRemoteEmployeeAPI(employeeId).orElseThrow(RemoteEmployeeApiException::new);
        return outputProjectService.loadProjectsAssignedToEmployee(employee.getEmployeeId());
    }

    @Override
    public List<Project> loadProjectsOfCompanyC(String companyId) throws RemoteCompanyApiException {
        Company company = getRemoteCompanyAPI(companyId).orElseThrow(RemoteCompanyApiException::new);
        return outputProjectService.loadProjectsOfCompanyC(company.getCompanyId());
    }

    @Override
    public List<Project> getAllProjects() {
        return outputProjectService.getAllProjects();
    }

    @Override
    public Optional<Company> getRemoteCompanyAPI(String companyId) throws RemoteCompanyApiException {
        Company company = outputCompanyAPIService.getRemoteCompanyAPI(companyId).orElseThrow(RemoteCompanyApiException::new);
        return Optional.of(company);
    }

    @Override
    public Optional<Employee> getRemoteEmployeeAPI(String employeeId) throws RemoteEmployeeApiException {
        Employee employee = outputEmployeeAPIService.getRemoteEmployeeAPI(employeeId).orElseThrow(RemoteEmployeeApiException::new);
        return Optional.of(employee);
    }
}
