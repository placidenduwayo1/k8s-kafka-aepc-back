package fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;

import java.util.List;
import java.util.Optional;

public interface OutputAddressService {
    Address consumeAddressAdd(Address address, String topic);
    Address saveInDbConsumedAddress(Address address);
    List<Address> findAddressByInfo(AddressDto addressDto);
    List<Address> getAllAddresses();
   Optional<Address> getAddress(String addressID) throws AddressNotFoundException;
    List<Address> getAddressesOfGivenCity(String city) throws AddressCityNotFoundException;
   Address consumeAddressDelete(Address address, String topic);
   String deleteAddress(String addressId) throws AddressNotFoundException;
    Address consumeAddressEdit(Address address,  String topic);
    Address updateAddress(Address address);
}
