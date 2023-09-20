package fr.placide.k8saepccleanarchibsmsaddress.domain.usecase;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressAlreadyExistsException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.KafkaProducerAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.output.OutputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class UseCaseTest {
    @Mock
    private OutputAddressService outputAddressServiceMock;
    @Mock
    private KafkaProducerAddressService kafkaProducerAddressServiceMock;
    @InjectMocks
    private UseCase underTest;
    private Address address;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        address = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes", "France"
        );
    }

    @Test
    void produceAndConsumeAddress() throws AddressAlreadyExistsException {
        AddressDto addressDto = new AddressDto(
                184, "Avenue de Liège",
                59300, "Valenciennes", "France");
        Mockito.when(kafkaProducerAddressServiceMock.sendKafkaAddressAddEvent(any(Address.class)))
                .thenReturn(address);
        Address consumedAddress = underTest.produceAndConsumeAddressAdd(addressDto);

        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(kafkaProducerAddressServiceMock, Mockito.atLeast(1))
                        .sendKafkaAddressAddEvent(Mockito.any(Address.class)),
                () -> Assertions.assertNotNull(consumedAddress));
    }
    @Test
    void saveInDbConsumedAddress() {
        Address newAddress = new Address(
                address.getAddressId(),
                184, "Avenue de Liège",
                59300, "Valenciennes", "France");
        Mockito.when(outputAddressServiceMock.saveInDbConsumedAddress(newAddress))
                .thenReturn(address);
        Address savedAddress = underTest.saveInDbConsumedAddress(newAddress);
        Assertions.assertAll("props", () -> {
            Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).saveInDbConsumedAddress(newAddress);
            Assertions.assertEquals(address, savedAddress);
        });
    }
    @Test
    void findAddressByInfo() {
        AddressDto addressDto = new AddressDto(
                1, "rue_test", 10000, "city-test", "country-test");
        List<Address> addresses = List.of();
        Mockito.when(outputAddressServiceMock.findAddressByInfo(addressDto)).thenReturn(addresses);
        List<Address> actual = underTest.findAddressByInfo(addressDto);
        Assertions.assertAll("props", () -> {
            Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).findAddressByInfo(addressDto);
            Assertions.assertEquals(0, actual.size());
        });
    }
    @Test
    void getAllAddresses() {
        List<Address> addresses = List.of(address, address, address);
        Mockito.when(outputAddressServiceMock.getAllAddresses()).thenReturn(addresses);
        List<Address> actualAddresses = underTest.getAllAddresses();
        Assertions.assertAll("props", () -> {
            Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).getAllAddresses();
            Assertions.assertEquals(3, actualAddresses.size());
        });
    }

    @Test
    void getAddress() throws AddressNotFoundException {
        Address newAddress = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        Mockito.when(outputAddressServiceMock.getAddress("1L")).thenReturn(Optional.of(newAddress));
        Optional<Address> foundAddress = underTest.getAddress("1L");
        Assertions.assertAll("props", () -> {
            Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).getAddress("1L");
            Assertions.assertNotNull(foundAddress);
        });
    }

    @Test
    void produceAndConsumeAddressId() throws AddressNotFoundException {
        String addressId = UUID.randomUUID().toString();
        Mockito.when(kafkaProducerAddressServiceMock.sendKafkaAddressDeleteEvent(addressId)).thenReturn(address);
        Address actual = underTest.produceAndConsumeAddressDelete(addressId);
        Assertions.assertAll("props", () -> {
            Mockito.verify(kafkaProducerAddressServiceMock, Mockito.atLeast(1)).sendKafkaAddressDeleteEvent(addressId);
            Assertions.assertEquals(address, actual);
        });
    }

    @Test
    void deleteAddress() throws AddressNotFoundException {
        //PREPARE
        String addressId = UUID.randomUUID().toString();
        //EXECUTE
        Mockito.when(outputAddressServiceMock.getAddress(addressId))
                .thenReturn(Optional.of(address));
        Address actualAddress = underTest.getAddress(addressId).orElseThrow(AddressNotFoundException::new);
        Mockito.when(outputAddressServiceMock.deleteAddress(addressId))
                .thenReturn("address " + address + " successfully deleted");

        String actualMessage = underTest.deleteAddress(addressId);
        //VERIFY

        Assertions.assertAll("props",
                () -> Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).deleteAddress(address.getAddressId()),
                () -> Mockito.verify(outputAddressServiceMock, Mockito.atLeast(1)).getAddress(addressId),
                () -> Assertions.assertNotNull(actualMessage),
                () -> Assertions.assertEquals(address, actualAddress));
    }

    @Test
    void produceAndConsumeAddressEdit() throws AddressNotFoundException {
        //PREPARE
        AddressDto addressDto = new AddressDto(
                184, "Avenue de Liège",
                59300, "Valenciennes", "France");
        String id = "uuid-0001";
        //EXECUTE
        Mockito.when(kafkaProducerAddressServiceMock.sendKafkaAddressEditEvent(addressDto, id))
                .thenReturn(address);
        Address actualAddress = underTest.produceAndConsumeAddressEdit(addressDto, id);
        //VERIFY
        Mockito.verify(kafkaProducerAddressServiceMock).sendKafkaAddressEditEvent(addressDto, id);
        Assertions.assertAll("prop",
                () -> Assertions.assertSame("Valenciennes", actualAddress.getCity()),
                () -> Assertions.assertEquals(address, actualAddress));
    }

    @Test
    void editAddress() {
        // PREPARE
        Address updatedAddress = new Address(
                UUID.randomUUID().toString(),
                10, "Avenue de Liège",
                59300, "Valenciennes", "France");
        //EXECUTE
        Mockito.when(outputAddressServiceMock.updateAddress(address)).thenReturn(updatedAddress);
        Address actualAddress = underTest.editAddress(address);
        Assertions.assertAll("props",
                () -> Assertions.assertNotEquals(address, actualAddress),
                () -> Assertions.assertNotEquals(address.getNum(), actualAddress.getNum()),
                () -> Mockito.verify(outputAddressServiceMock).updateAddress(address)
        );
    }

    @Test
    void getAddressesOfGivenCity() throws AddressCityNotFoundException {
        //PREPARE
        String city = "Valenciennes";
        List<Address> addresses = List.of(address, address, address);
        //EXECUTE
        Mockito.when(outputAddressServiceMock.getAddressesOfGivenCity(city)).thenReturn(addresses);
        List<Address> obtained = underTest.getAddressesOfGivenCity(city);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(outputAddressServiceMock).getAddressesOfGivenCity(city),
                () -> Assertions.assertFalse(obtained.isEmpty()));
    }
}