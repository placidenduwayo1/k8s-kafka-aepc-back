package fr.placide.k8saepccleanarchibsmsaddress.infra.output.service;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.mapper.AddressMapper;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.repository.AddressRepository;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.service.OutputAddressServiceImplementation;
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

class OutputAddressServiceImplementationTest {
    @Mock
    private AddressRepository addressRepositoryMock;
    @InjectMocks
    private OutputAddressServiceImplementation underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consumeAddressAdd() {
        //PREPARE
        Address addressVal = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        //EXECUTE
        Address address = underTest.consumeAddressAdd(addressVal, "topic1");
        //VERIFY
        Assertions.assertNotNull(address);
    }

    @Test
    void saveInDbConsumedAddress() {
        //PREPARE
        Address address = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        AddressModel addressModel = new AddressModel(UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        AddressModel mappedAddressModel = AddressMapper.mapBeanToModel(address);
        //EXECUTE
        Mockito.when(addressRepositoryMock.save(mappedAddressModel)).thenReturn(addressModel);
        Address actualAddress = underTest.saveInDbConsumedAddress(address);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).save(mappedAddressModel),
                () -> Assertions.assertEquals("Valenciennes", actualAddress.getCity()));
    }

    @Test
    void findAddressByInfo() {
        //PREPARE
        int num = 184;
        String street = "Avenue de Liège";
        int poBox = 59300;
        String city = "Valenciennes";
        String country = "France";
        AddressModel addressVal = new AddressModel(
                UUID.randomUUID().toString(),
                num, street, poBox, city, country);

        List<AddressModel> models = List.of(addressVal);

        AddressDto addressDto = new AddressDto(num, street, poBox, city, country);
        //EXECUTE
        Mockito.when(addressRepositoryMock.findByNumAndStreetAndPoBoxAndCityAndCountry(num, street, poBox, city, country)).thenReturn(models);
        List<Address> actualList = underTest.findAddressByInfo(addressDto);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1))
                        .findByNumAndStreetAndPoBoxAndCityAndCountry(num, street, poBox, city, country),
                () -> Assertions.assertEquals(1, actualList.size()));
    }

    @Test
    void getAllAddresses() {
        //PREPARE
        AddressModel addressVal = new AddressModel(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        AddressModel addressParis = new AddressModel(
                UUID.randomUUID().toString(),
                44, "Rue Notre Dame des Victoires",
                74002, "Paris",
                "France");

        List<AddressModel> addressModels = List.of(addressVal, addressParis);

        //EXECUTE
        Mockito.when(addressRepositoryMock.findAll())
                .thenReturn(addressModels);
        List<Address> actualAddresses = underTest.getAllAddresses();
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).findAll(),
                () -> Assertions.assertEquals(2, actualAddresses.size()));
    }

    @Test
    void getAddress() throws AddressNotFoundException {
        //PREPARE
        Optional<AddressModel> addressVal = Optional.of(new AddressModel(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France"));
        String id = "uuid-001";
        //EXECUTE
        Mockito
                .when(addressRepositoryMock.findById(id))
                .thenReturn(addressVal);
        Optional<Address> actualAddress = underTest.getAddress(id);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).findById(id),
                () -> Assertions.assertNotNull(actualAddress));
    }

    @Test
    void consumeAddressDelete() {
        //PREPARE
        Address addressVal = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        //EXECUTE
        Address address = underTest.consumeAddressDelete(addressVal, "topic2");
        //VERIFY
        Assertions.assertNotNull(address);
    }

    @Test
    void deleteAddress() throws AddressNotFoundException {
        //PREPARE
        String id = "uuid-001";
        Optional<AddressModel> addressVal =
                Optional.of(new AddressModel(id,
                        184, "Avenue de Liège",
                        59300, "Valenciennes",
                        "France"));

        //EXECUTE
        Mockito.when(addressRepositoryMock.findById(id)).thenReturn(addressVal);
        Address address = underTest.getAddress(id).orElseThrow(AddressNotFoundException::new);
        Address consumedAddress = underTest.consumeAddressDelete(address, "topic2");
        String deletedAddress = underTest.deleteAddress(consumedAddress.getAddressId());

        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).deleteById(consumedAddress.getAddressId()),
                () -> Assertions.assertNotNull(deletedAddress));
    }

    @Test
    void consumeAddressEdit() {
        //PREPARE
        Address addressVal = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        //EXECUTE
        Address address = underTest.consumeAddressEdit(addressVal, "topic3");
        //VERIFY
        Assertions.assertNotNull(address);
    }

    @Test
    void updateAddress() {
        //PREPARE
        Address addressVal = new Address(
                UUID.randomUUID().toString(),
                184, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        AddressModel addressModel = AddressMapper.mapBeanToModel(addressVal);
        Address expectedAddress = new Address(
                UUID.randomUUID().toString(),
                5, "Avenue de Liège",
                59300, "Valenciennes",
                "France");
        AddressModel expectedAddressModel = AddressMapper.mapBeanToModel(expectedAddress);
        //EXECUTE
        Mockito.when(addressRepositoryMock.save(addressModel)).thenReturn(expectedAddressModel);
        Address address = underTest.updateAddress(addressVal);
        //VERIFY
        Assertions.assertAll("gpe of assertions",
                () -> Mockito.verify(addressRepositoryMock, Mockito.atLeast(1)).save(addressModel),
                () -> Assertions.assertEquals(5, address.getNum()));
    }
}