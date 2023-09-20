package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.ProjectNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteEmployeeApiException;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.OutputProjectService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputCompanyAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output.RemoteOutputEmployeeAPIService;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.CompanyServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.proxies.EmployeeServiceProxy;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers.Mapper;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OutputProjectServiceImpl implements OutputProjectService, RemoteOutputEmployeeAPIService, RemoteOutputCompanyAPIService {
    private final ProjectRepository repository;
    private final EmployeeServiceProxy employeeServiceProxy;
    private final CompanyServiceProxy companyServiceProxy;

    public OutputProjectServiceImpl(ProjectRepository repository,
                                    @Qualifier(value = "employee-service-proxy") EmployeeServiceProxy employeeServiceProxy,
                                    @Qualifier(value = "company-service-proxy") CompanyServiceProxy companyServiceProxy) {
        this.repository = repository;
        this.employeeServiceProxy = employeeServiceProxy;
        this.companyServiceProxy = companyServiceProxy;
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic1}", groupId = "${spring.kafka.consumer.group-id}")
    public Project consumeKafkaEventProjectCreate(@Payload Project bean, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("employee to delete:<{}> consumed from topic:<{}>",bean,topic);
        return bean;
    }

    @Override
    public Project saveProject(Project project) {
        Project consumed = consumeKafkaEventProjectCreate(project,"${topics.names.topic1}");
        ProjectModel saved = repository.save(Mapper.fromTo(consumed));
        return Mapper.fromTo(saved);
    }

    @Override
    public Optional<Project> getProject(String projectId) throws ProjectNotFoundException {
        ProjectModel model = repository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        return Optional.of(Mapper.fromTo(model));
    }

    @Override
    public List<Project> loadProjectByInfo(String name, String desc, String state, String employeeId, String companyId) {
        return mapToBean(repository.findByNameAndDescriptionAndStateAndEmployeeIdAndCompanyId(name,desc,state,employeeId,companyId));
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic2}", groupId = "${spring.kafka.consumer.group-id}")
    public Project consumeKafkaEventProjectDelete(Project payload, String topic){
        log.info("project to delete :<{}> consumed from topic:<{}>",payload,topic);
        return payload;
    }

    @Override
    public String deleteProject(String projectId) throws ProjectNotFoundException {
        Project saved = getProject(projectId).orElseThrow(ProjectNotFoundException::new);
        Project consumed = consumeKafkaEventProjectDelete(saved,"${topics.names.topic2}");
        repository.deleteById(consumed.getProjectId());
        return "project <"+consumed+"> is deleted";
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic3}", groupId = "${spring.kafka.consumer.group-id}")
    public Project consumeKafkaEventProjectUpdate(@Payload Project payload, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("project to update:<{}> consumed from topic:<{}>",payload,topic);
        return payload;
    }

    @Override
    public Project updateProject(Project payload) {
        Project consumed = consumeKafkaEventProjectUpdate(payload,"${topics.names.topic3}");
        ProjectModel pModel = Mapper.fromTo(consumed);
        return Mapper.fromTo(repository.save(pModel));
    }

    @Override
    public List<Project> loadProjectsAssignedToEmployee(String employeeId) {
        return mapToBean(repository.findByEmployeeId(employeeId));
    }

    @Override
    public List<Project> loadProjectsOfCompanyC(String companyId) {
        return mapToBean(repository.findByCompanyId(companyId));
    }

    @Override
    public List<Project> getAllProjects() {
        return mapToBean(repository.findAll());
    }

    @Override
    public Optional<Company> getRemoteCompanyAPI(String companyId) throws RemoteCompanyApiException {
        CompanyModel model = companyServiceProxy.loadRemoteApiGetCompany(companyId).orElseThrow(RemoteCompanyApiException::new);
        return Optional.of(Mapper.fromTo(model));
    }
    @Override
    public Optional<Employee> getRemoteEmployeeAPI(String employeeId) throws RemoteEmployeeApiException {
        EmployeeModel model =employeeServiceProxy.loadRemoteApiGetEmployee(employeeId).orElseThrow(RemoteEmployeeApiException::new);
        return Optional.of(Mapper.fromTo(model));
    }

    private List<Project> mapToBean(List<ProjectModel> models){
        return models.stream()
                .map(Mapper::fromTo)
                .toList();
    }
}
