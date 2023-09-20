package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.mapper;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyModel;
import org.springframework.beans.BeanUtils;

public class CompanyMapper {
    private CompanyMapper(){}
    public static CompanyModel fromBeanToModel(Company company){
        CompanyModel model = new CompanyModel();
        BeanUtils.copyProperties(company, model);
        return model;
    }
    public static Company fromModelToBean(CompanyModel model) {
        Company bean = new Company();
        BeanUtils.copyProperties(model,bean);
        return bean;
    }
    public static Company fromDtoToBean(CompanyDto dto){
        Company bean = new Company();
        BeanUtils.copyProperties(dto,bean);
        return bean;
    }

    public static CompanyDto fromBeanToDto(Company company) {
        CompanyDto dto = new CompanyDto();
        BeanUtils.copyProperties(company,dto);
        return dto;
    }
}
