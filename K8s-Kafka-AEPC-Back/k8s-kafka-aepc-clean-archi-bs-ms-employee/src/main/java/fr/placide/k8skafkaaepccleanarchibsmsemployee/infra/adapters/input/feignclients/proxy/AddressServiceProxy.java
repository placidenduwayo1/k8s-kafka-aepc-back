package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions.RemoteApiAddressNotLoadedException;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.fallback.AddressServiceProxyFallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "k8s-kafka-aepc-bs-ms-address", fallback = AddressServiceProxyFallback.class)
@Qualifier(value = "address-service-proxy")
public interface AddressServiceProxy {
    @GetMapping(value = "/addresses/id/{addressId}")
    Optional<AddressModel> loadRemoteApiGetAddressById(@PathVariable String addressId) throws RemoteApiAddressNotLoadedException;
    @GetMapping(value = "/addresses")
    List<AddressModel> loadRemoteAddressIpiGetAllAddresses();
}
