package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;

public interface OutputProjectKafkaProducerService {
    Project produceKafkaEventProjectCreate(Project project);
    Project produceKafkaEventProjectDelete(String projectId) throws ProjectNotFoundException;
    Project produceKafkaEventProjectEdit(ProjectDto dto, String projectId) throws ProjectNotFoundException,
            RemoteEmployeeApiException, RemoteCompanyApiException;
}
