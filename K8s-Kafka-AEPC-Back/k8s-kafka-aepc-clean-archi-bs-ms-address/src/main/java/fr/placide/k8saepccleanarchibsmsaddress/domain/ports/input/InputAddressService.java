package fr.placide.k8saepccleanarchibsmsaddress.domain.ports.input;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressAlreadyExistsException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressFieldsInvalidException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;

import java.util.List;
import java.util.Optional;


public interface InputAddressService {
    Address produceAndConsumeAddressAdd(AddressDto addressDto) throws
            AddressFieldsInvalidException,
            AddressAlreadyExistsException;
    Address saveInDbConsumedAddress(Address address);
    List<Address> findAddressByInfo(AddressDto addressDto);
    List<Address> getAllAddresses();
    Optional<Address> getAddress(String addressID) throws AddressNotFoundException;
    List<Address> getAddressesOfGivenCity(String city) throws AddressCityNotFoundException;
    Address produceAndConsumeAddressDelete(String addressId) throws AddressNotFoundException;
    String deleteAddress (String addressId) throws AddressNotFoundException;
    Address produceAndConsumeAddressEdit(AddressDto payload, String addressId) throws
            AddressNotFoundException;
    Address editAddress(Address address);

}
