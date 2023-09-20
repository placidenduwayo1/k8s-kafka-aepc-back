package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.output.mapper.address;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import org.springframework.beans.BeanUtils;

public class AddressMapper {
    private AddressMapper(){}
    public static Address toBean(AddressModel model){
        Address bean = new Address();
        BeanUtils.copyProperties(model,bean);
        return bean;
    }
    public static AddressModel toModel(Address bean){
        AddressModel model = new AddressModel();
        BeanUtils.copyProperties(bean, model);
        return model;
    }
}
