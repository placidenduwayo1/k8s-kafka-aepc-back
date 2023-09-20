package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;

import java.util.List;

public interface RemoteInputAddressService {
    Address getRemoteAddressById(String addressId) throws RemoteApiAddressNotLoadedException;
    List<Address> loadRemoteAllAddresses();
}
