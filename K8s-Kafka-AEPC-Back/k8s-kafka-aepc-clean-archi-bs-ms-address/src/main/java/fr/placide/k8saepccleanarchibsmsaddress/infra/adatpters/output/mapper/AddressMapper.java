package fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import org.springframework.beans.BeanUtils;

public class AddressMapper {
    private AddressMapper(){}
    public static Address mapModelToBean(AddressModel addressModel){
        Address address = new Address();
        BeanUtils.copyProperties(addressModel, address);
        return address;
    }

    public static AddressModel mapBeanToModel(Address address){
        AddressModel addressModel = new AddressModel();
        BeanUtils.copyProperties(address, addressModel);
        return addressModel;
    }

    public static Address mapDtoToBean(AddressDto addressDto) {
        Address address = new Address();
        BeanUtils.copyProperties(addressDto, address);
        return address;
    }

    public static AddressDto mapBeanBeanToDto(Address address) {
        AddressDto addressDto = new AddressDto();
        BeanUtils.copyProperties(address, addressDto);
        return addressDto;
    }
}
