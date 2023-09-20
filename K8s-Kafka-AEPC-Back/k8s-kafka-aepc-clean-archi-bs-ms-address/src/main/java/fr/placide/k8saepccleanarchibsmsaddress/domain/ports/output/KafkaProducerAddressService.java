package fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;

public interface KafkaProducerAddressService {
    Address sendKafkaAddressAddEvent(Address address);
    Address sendKafkaAddressDeleteEvent(String addressID) throws AddressNotFoundException;
    Address sendKafkaAddressEditEvent(AddressDto payload, String addressId) throws AddressNotFoundException;
}
