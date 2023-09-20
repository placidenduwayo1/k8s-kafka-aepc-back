package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.mappers;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Project;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.input.feignclients.models.EmployeeModel;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import org.springframework.beans.BeanUtils;

public class Mapper {
    private Mapper(){}
    public static ProjectModel fromTo(Project bean){
        ProjectModel model = new ProjectModel();
        BeanUtils.copyProperties(bean, model);
        return model;
    }
    public static Project fromTo(ProjectModel model){
        Project bean = new Project();
        BeanUtils.copyProperties(model, bean);
        return bean;
    }
    public static Project fromTo(ProjectDto dto){
        Project bean = new Project();
        BeanUtils.copyProperties(dto, bean);
        return bean;
    }
    public static EmployeeModel fromTo(Employee bean){
        EmployeeModel model = new EmployeeModel();
        BeanUtils.copyProperties(bean, model);
        return model;
    }
    public static Employee fromTo(EmployeeModel model){
        Employee bean = new Employee();
        BeanUtils.copyProperties(model, bean);
        return bean;
    }
    public static CompanyModel fromTo(Company bean){
        CompanyModel model = new CompanyModel();
        BeanUtils.copyProperties(bean,model);
        return model;
    }
    public static Company fromTo(CompanyModel model){
        Company bean = new Company();
        BeanUtils.copyProperties(model,bean);
        return bean;
    }

    public static ProjectDto fromBeanToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        BeanUtils.copyProperties(project,dto);
        return dto;
    }
}
