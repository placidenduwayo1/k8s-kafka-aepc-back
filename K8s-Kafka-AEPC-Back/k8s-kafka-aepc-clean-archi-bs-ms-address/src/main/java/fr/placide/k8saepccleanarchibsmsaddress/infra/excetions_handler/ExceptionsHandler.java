package fr.placide.k8saepccleanarchibsmsaddress.infra.excetions_handler;

import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressAlreadyExistsException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressCityNotFoundException;
import fr.placide.k8saepccleanarchibsmsaddress.domain.msg.Message;
import fr.placide.k8saepccleanarchibsmsaddress.domain.exceptions.AddressFieldsInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler(value = AddressAlreadyExistsException.class)
    public ResponseEntity<String> handleAddressAlreadyExistsException(){
        return new ResponseEntity<>(Message
                .ADDRESS_ALREADY_EXISTS_EXCEPTION
                .getMsg(),
                HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(value = AddressFieldsInvalidException.class)
    public ResponseEntity<String> handleAddressFieldsInvalidException(){
        return new ResponseEntity<>(Message
                .ADDRESS_FIELDS_INVALID_EXCEPTION
                .getMsg(),
                HttpStatus.NOT_ACCEPTABLE);

    }
    @ExceptionHandler(value = AddressNotFoundException.class)
    public ResponseEntity<String> handleAddressNotFoundException(){
        return new ResponseEntity<>(Message
                .ADDRESS_NOT_FOUND_EXCEPTION
                .getMsg(),
                HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = AddressCityNotFoundException.class)
    public ResponseEntity<String> handleAddressUnknownCityException(){
        return new ResponseEntity<>(Message
                .ADDRESS_CITY_NOT_EXCEPTION
                .getMsg(),
                HttpStatus.NOT_FOUND);
    }
}
