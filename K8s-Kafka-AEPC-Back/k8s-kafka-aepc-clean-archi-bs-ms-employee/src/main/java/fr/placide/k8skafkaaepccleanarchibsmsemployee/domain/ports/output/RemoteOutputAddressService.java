package fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.beans.address.Address;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;

import java.util.List;

public interface RemoteOutputAddressService {
    Address getRemoteAddressById(String addressId) throws RemoteApiAddressNotLoadedException;
    List<Address> loadAllRemoteAddresses();
}
