package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.*;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import java.util.List;
import java.util.Optional;

public interface InputProjectService {
    Project produceKafkaEventProjectCreate(ProjectDto dto) throws ProjectAlreadyExistsException,
            ProjectPriorityInvalidException, ProjectStateInvalidException, RemoteEmployeeApiException, RemoteCompanyApiException,
            ProjectFieldsEmptyException;
    Project createProject(Project project);
    Optional<Project> getProject(String projectId) throws ProjectNotFoundException;
    List<Project> loadProjectByInfo(String name, String desc, String state,String employeeId, String companyId);
    Project produceKafkaEventProjectDelete(String projectId) throws ProjectNotFoundException;
    String deleteProject(String projectId) throws ProjectNotFoundException;
    Project produceKafkaEventProjectUpdate(ProjectDto payload, String projectId) throws ProjectNotFoundException,
            ProjectPriorityInvalidException, ProjectStateInvalidException, RemoteEmployeeApiException, RemoteCompanyApiException,
            ProjectFieldsEmptyException;
    Project updateProject (Project payload);
    List<Project> loadProjectsAssignedToEmployee(String employeeId) throws RemoteEmployeeApiException;
    List<Project> loadProjectsOfCompanyC(String companyId) throws RemoteCompanyApiException;

    List<Project> getAllProjects();
}
