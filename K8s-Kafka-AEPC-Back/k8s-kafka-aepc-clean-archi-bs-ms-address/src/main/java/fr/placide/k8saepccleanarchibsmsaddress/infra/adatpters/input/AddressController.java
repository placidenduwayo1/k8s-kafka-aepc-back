package fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.input;

import fr.placide.k8saepccleanarchibsmsaddress.domain.bean.Address;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressAlreadyExistsException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressFieldsInvalidException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.ports.input.InputAddressService;
import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AddressController {

    private final InputAddressService inputAddressService;

    @PostMapping(value = "/addresses")
    public ResponseEntity<Object> produceAndConsumeAddress(@RequestBody AddressDto addressDto) throws
            AddressAlreadyExistsException, AddressFieldsInvalidException {

        Address producedConsumedAddress = inputAddressService.produceAndConsumeAddressAdd(addressDto);
        Address savedAddress = inputAddressService.saveInDbConsumedAddress(producedConsumedAddress);
        return new ResponseEntity<>(String
                .format("%s is sent and consumed;%n %s is saved in db", producedConsumedAddress, savedAddress),
                HttpStatus.OK);
    }

    @GetMapping(value = "/addresses")
    public List<Address> getAllAddresses() {
        return inputAddressService.getAllAddresses();
    }

    @GetMapping(value = "/addresses/city/{city}")
    public List<Address> getAllAddressesForGivenCity(@PathVariable(name = "city") String city) throws
            AddressCityNotFoundException {
       return inputAddressService.getAddressesOfGivenCity(city);
    }

    @GetMapping(value = "/addresses/id/{addressID}")
    public Address getAddress(@PathVariable(name = "addressID") String addressID) throws
            AddressNotFoundException {
        return inputAddressService.getAddress(addressID).orElseThrow(
                AddressNotFoundException::new
        );
    }

    @DeleteMapping(value = "/addresses/id/{addressID}")
    public ResponseEntity<Object> deleteAddress(@PathVariable(name = "addressID") String addressID) throws
            AddressNotFoundException {
        Address consumedAddress = inputAddressService.produceAndConsumeAddressDelete(addressID);
        inputAddressService.deleteAddress(consumedAddress.getAddressId());
        return new ResponseEntity<>(String.format("address %s is sent to topic;%n address with id=<%s> is deleted",
                consumedAddress, addressID), HttpStatus.OK);
    }

    @PutMapping(value = "/addresses/id/{addressId}")
    public ResponseEntity<Object> editAddress(@RequestBody AddressDto addressDto,
                                              @PathVariable(name = "addressId") String addressId) throws
            AddressNotFoundException {

        Address produceAndConsumeAddress = inputAddressService.produceAndConsumeAddressEdit(addressDto, addressId);
        Address savedAddress = inputAddressService.editAddress(produceAndConsumeAddress);

        return new ResponseEntity<>(String
                .format("%s to update is sent and consumed;%n %s is updated in db", produceAndConsumeAddress, savedAddress),
                HttpStatus.OK);
    }
}
