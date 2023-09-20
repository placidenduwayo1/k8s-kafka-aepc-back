package fr.placide.k8saepccleanarchibsmsaddress.domain.usecase;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressAlreadyExistsException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressFieldsInvalidException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.input.InputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.KafkaProducerAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.OutputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper.AddressMapper;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class UseCase implements InputAddressService {
    private final OutputAddressService outputAddressService;
    private final KafkaProducerAddressService kafkaProducerAddressService;

    public UseCase(
            KafkaProducerAddressService kafkaProducerAddressService,
            OutputAddressService outputAddressService) {

        this.kafkaProducerAddressService = kafkaProducerAddressService;
        this.outputAddressService = outputAddressService;
    }

    private void innerCheckAddress(AddressDto addressDto) {
        if (Validator.isInvalidAddress(addressDto)) {
            throw new AddressFieldsInvalidException();
        }
    }

    /*send address event to kafka topic and consumer consumes it*/
    @Override
    public Address produceAndConsumeAddressAdd(AddressDto addressDto) throws
            AddressFieldsInvalidException, AddressAlreadyExistsException {

        Validator.formatAddress(addressDto);
        innerCheckAddress(addressDto);
        if (!findAddressByInfo(addressDto).isEmpty()) {
            throw new AddressAlreadyExistsException();
        }

        Address address = AddressMapper.mapDtoToBean(addressDto);
        return kafkaProducerAddressService.sendKafkaAddressAddEvent(address);
    }

    @Override
    public Address saveInDbConsumedAddress(Address address) {
        return outputAddressService.saveInDbConsumedAddress(address);
    }

    @Override
    public List<Address> findAddressByInfo(AddressDto addressDto) {
        return outputAddressService.findAddressByInfo(addressDto);
    }

    @Override
    public List<Address> getAllAddresses() {
        return outputAddressService.getAllAddresses();
    }

    @Override
    public Optional<Address> getAddress(String addressID) throws AddressNotFoundException {
       Address address = outputAddressService.getAddress(addressID).orElseThrow(
               AddressNotFoundException::new
       );

        return Optional.of(address);
    }
    @Override
    public List<Address> getAddressesOfGivenCity(String city) throws AddressCityNotFoundException {

        List<Address> addresses = outputAddressService.getAddressesOfGivenCity(city);
        for(Address address: addresses){
            if(!address.getCity().equals(city)){
                throw new AddressCityNotFoundException();
            }
        }
        UnaryOperator<Address> mapper = (var address)->{
            address.setAddressId("<"+address.getAddressId()+">");
            address.setStreet("<"+address.getStreet()+">");
            address.setCity("<"+city+">");
            address.setCountry("<"+address.getCountry()+">");
            return address;
        };

        return addresses.stream()
                .map(mapper)
                .toList();
    }
    /*send addressId event to kafka topic and consumer consumes it*/
    @Override
    public Address produceAndConsumeAddressDelete(String addressId) throws AddressNotFoundException {
        return kafkaProducerAddressService.sendKafkaAddressDeleteEvent(addressId);
    }
    @Override
    public String deleteAddress(String addressId) throws AddressNotFoundException {
       Address address = getAddress(addressId).orElseThrow(AddressNotFoundException::new);
        outputAddressService.deleteAddress(address.getAddressId());
        return "Address <"+address+"> successfully deleted";
    }
    @Override
    public Address produceAndConsumeAddressEdit(AddressDto payload, String addressId) throws
            AddressNotFoundException {
        Validator.formatAddress(payload);
        innerCheckAddress(payload);
        return kafkaProducerAddressService.sendKafkaAddressEditEvent(payload, addressId);
    }
    @Override
    public Address editAddress(Address address) {
        return outputAddressService.updateAddress(address);
    }

}
