package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.employee;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.employee.Employee;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeDto;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.models.EmployeeModel;
import org.springframework.beans.BeanUtils;

public class EmployeeMapper {
    private EmployeeMapper(){}

    public static Employee toBean(EmployeeModel model){
        Employee employee = new Employee();
        BeanUtils.copyProperties(model,employee);
        return employee;
    }
    public static EmployeeModel toModel(Employee bean){
        EmployeeModel model = new EmployeeModel();
        BeanUtils.copyProperties(bean, model);
        return model;
    }

    public static Employee fromDto(EmployeeDto dto){
        Employee bean = new Employee();
        BeanUtils.copyProperties(dto,bean);
        return bean;
    }

    public static EmployeeDto fromBeanToDto(Employee bean) {
        EmployeeDto dto = new EmployeeDto();
        BeanUtils.copyProperties(bean,dto);
        return dto;
    }
}
