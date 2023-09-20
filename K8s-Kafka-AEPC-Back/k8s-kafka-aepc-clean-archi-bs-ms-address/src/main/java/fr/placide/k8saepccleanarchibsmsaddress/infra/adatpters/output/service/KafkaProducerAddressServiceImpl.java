package fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.service;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.KafkaProducerAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.repository.AddressRepository;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper.AddressMapper;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaProducerAddressServiceImpl implements KafkaProducerAddressService {

    private final KafkaTemplate<String, Address> addressKafkaTemplate;
    private final AddressRepository addressRepository;
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;
    @Override
    public Address sendKafkaAddressAddEvent(Address address) {
        address.setAddressId(UUID.randomUUID().toString());
        Message<Address> addressMessage = MessageBuilder
                .withPayload(address)
                .setHeader(KafkaHeaders.TOPIC, topic1)
                .build();
        addressKafkaTemplate.send(addressMessage);

        return address;
    }

    @Override
    public Address sendKafkaAddressDeleteEvent(String addressID) throws AddressNotFoundException {
       AddressModel model = addressRepository.findById(addressID).orElseThrow(AddressNotFoundException::new);
        Address address = AddressMapper.mapModelToBean(model);
        Message<Address> addressMessage = MessageBuilder
                .withPayload(address)
                .setHeader(KafkaHeaders.TOPIC, topic2)
                .build();

        addressKafkaTemplate.send(addressMessage);
        return address;
    }

    @Override
    public Address sendKafkaAddressEditEvent(AddressDto dto, String addressId) throws
            AddressNotFoundException {

        Optional<AddressModel> model = addressRepository.findById(addressId);
        if(model.isEmpty()){
            throw new AddressNotFoundException();
        }
        Address mappedAddress = AddressMapper.mapDtoToBean(dto);
        mappedAddress.setAddressId(addressId);
        Message<Address> message = MessageBuilder
                .withPayload(mappedAddress)
                .setHeader(KafkaHeaders.TOPIC, topic3)
                .build();

        addressKafkaTemplate.send(message);

        return mappedAddress;
    }
}
