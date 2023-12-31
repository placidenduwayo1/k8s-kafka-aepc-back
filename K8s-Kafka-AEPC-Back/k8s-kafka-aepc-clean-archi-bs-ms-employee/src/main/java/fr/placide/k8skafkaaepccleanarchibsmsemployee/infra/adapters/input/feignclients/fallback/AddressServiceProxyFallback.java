package fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.fallback;

import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.models.AddressModel;
import fr.placide.k8skafkaaepccleanarchibsmsemployee.infra.adapters.input.feignclients.proxy.AddressServiceProxy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static fr.placide.k8skafkaaepccleanarchibsmsemployee.domain.exceptions
        .ExceptionsMsg.ADDRESS_API_UNREACHABLE;
@Component
public class AddressServiceProxyFallback implements AddressServiceProxy {
    @Override
    public Optional<AddressModel> loadRemoteApiGetAddressById(String addressId) {
       return Optional.of(AddressModel.builder()
                .addressId(ADDRESS_API_UNREACHABLE)
                .num(0)
                .street(ADDRESS_API_UNREACHABLE)
                .poBox(0)
                .city(ADDRESS_API_UNREACHABLE)
                .country(ADDRESS_API_UNREACHABLE)
                .build());
    }
    @Override
    public List<AddressModel> loadRemoteAddressIpiGetAllAddresses() {
        return List.of(AddressModel.builder()
                .addressId(ADDRESS_API_UNREACHABLE)
                .num(0)
                .street(ADDRESS_API_UNREACHABLE)
                .poBox(0)
                .city(ADDRESS_API_UNREACHABLE)
                .country(ADDRESS_API_UNREACHABLE)
                .build());
    }
}
