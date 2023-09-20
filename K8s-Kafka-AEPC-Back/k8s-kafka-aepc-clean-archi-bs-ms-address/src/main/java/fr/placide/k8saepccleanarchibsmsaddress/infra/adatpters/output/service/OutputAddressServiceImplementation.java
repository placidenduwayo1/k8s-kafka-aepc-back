package fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.service;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.OutputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper.AddressMapper;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.repository.AddressRepository;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutputAddressServiceImplementation implements OutputAddressService {

    private final AddressRepository addressRepository;
    @Override
    @KafkaListener(topics = "${topics.names.topic1}", groupId = "${spring.kafka.consumer.group-id}")
    public Address consumeAddressAdd(@Payload  Address address,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("address: {} to add consumed from topic: {}",address,topic);
       return address;
    }

    @Override
    public Address saveInDbConsumedAddress(Address address) {
        Address consumedAddress = consumeAddressAdd(address,"${topics.names.topic1}");
        AddressModel addressModel = AddressMapper.mapBeanToModel(consumedAddress);
        AddressModel savedAddress = addressRepository.save(addressModel);

        return AddressMapper.mapModelToBean(savedAddress);
    }

    @Override
    public List<Address> findAddressByInfo(AddressDto addressDto) {
        List<AddressModel> addressModels = addressRepository.findByNumAndStreetAndPoBoxAndCityAndCountry(
                addressDto.getNum(),
                addressDto.getStreet(),
                addressDto.getPoBox(),
                addressDto.getCity(),
                addressDto.getCountry()
        );

        return innerUtilityMethod(addressModels);

    }

    @Override
    public List<Address> getAllAddresses() {
        List<AddressModel> addressModels = addressRepository.findAll();
        return innerUtilityMethod(addressModels);
    }

    @Override
    public Optional<Address> getAddress(String addressID) throws AddressNotFoundException {
        AddressModel addressModel = addressRepository.findById(addressID).orElseThrow(
                AddressNotFoundException::new
        );
        return Optional.of(AddressMapper.mapModelToBean(addressModel));
    }

    @Override
    public List<Address> getAddressesOfGivenCity(String city) {
        List<AddressModel> addressModels = addressRepository.findByCity(city);

      return innerUtilityMethod(addressModels);
    }

    @Override
    @KafkaListener(topics = "${topics.names.topic2}", groupId = "${spring.kafka.consumer.group-id}")
    public Address consumeAddressDelete(@Payload Address address,
                                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("address: {} to delete consumed from topic: {}",address,topic);
        return address;
    }

    @Override
    public String deleteAddress(String addressId) throws AddressNotFoundException {
        Address address = getAddress(addressId).orElseThrow(
                AddressNotFoundException::new
        );
        Address consumedAddress = consumeAddressDelete(address,"${topics.names.topic2}");
        addressRepository.deleteById(consumedAddress.getAddressId());
        return "Address"+address+"successfully deleted";
    }
    @Override
    @KafkaListener(topics = "${topics.names.topic3}", groupId = "${spring.kafka.consumer.group-id}")
    public Address consumeAddressEdit(@Payload Address address,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("address: {} to edit consumed from topic: {}",address,topic);
        return address;
    }
    @Override
    public Address updateAddress(Address address) {
        Address consumedAddress = consumeAddressEdit(address,"${topics.names.topic3}");
        AddressModel addressModel = AddressMapper.mapBeanToModel(consumedAddress);
        return AddressMapper.mapModelToBean(addressRepository.save(addressModel));
    }

    private List<Address> innerUtilityMethod(List<AddressModel> addressModels) {
        Function<AddressModel, Address> mapper = AddressMapper::mapModelToBean;
        return addressModels
                .stream()
                .map(mapper)
                .toList();
    }
}