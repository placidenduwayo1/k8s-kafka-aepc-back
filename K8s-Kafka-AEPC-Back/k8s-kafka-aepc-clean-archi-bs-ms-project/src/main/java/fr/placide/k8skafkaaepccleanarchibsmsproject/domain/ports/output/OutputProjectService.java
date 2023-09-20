package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;

import java.util.List;
import java.util.Optional;

public interface OutputProjectService {
    Project consumeKafkaEventProjectCreate(Project bean, String topic);
    Project saveProject(Project project);
    Optional<Project> getProject(String projectId) throws ProjectNotFoundException;
    List<Project> loadProjectByInfo(String name, String description, String state, String employeeId, String companyId);
    Project consumeKafkaEventProjectDelete(Project payload,String topic);
    String deleteProject(String projectId) throws ProjectNotFoundException;
    Project consumeKafkaEventProjectUpdate(Project payload, String topic);
    Project updateProject (Project payload);
    List<Project> loadProjectsAssignedToEmployee(String employeeId);
    List<Project> loadProjectsOfCompanyC(String companyId);

    List<Project> getAllProjects();
}
