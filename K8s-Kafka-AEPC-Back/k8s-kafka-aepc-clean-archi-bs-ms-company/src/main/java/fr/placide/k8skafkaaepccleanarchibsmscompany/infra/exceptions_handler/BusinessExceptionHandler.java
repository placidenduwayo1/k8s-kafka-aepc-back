package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.exceptions_handler;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BusinessExceptionHandler {
    @ExceptionHandler(value = CompanyNotFoundException.class)
    public ResponseEntity<Object> handleCompanyNotFoundException(){
        return new ResponseEntity<>(ExceptionMessage.COMPANY_NOT_FOUND_EXCEPTION.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = CompanyEmptyFieldsException.class)
    public ResponseEntity<Object> handleCompanyEmptyFieldsException(){
        return new ResponseEntity<>(ExceptionMessage.COMPANY_FIELDS_EMPTY_EXCEPTION.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = CompanyAlreadyExistsException.class)
    public ResponseEntity<Object> handleCompanyAlreadyExistsException(){
        return new ResponseEntity<>(ExceptionMessage.COMPANY_ALREADY_EXISTS_EXCEPTION.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = CompanyTypeInvalidException.class)
    public ResponseEntity<Object> handleCompanyTypeInvalidException(){
        return new ResponseEntity<>(ExceptionMessage.COMPANY_TYPE_UNKNOWN_EXCEPTION.getMessage(), HttpStatus.NOT_FOUND);
    }
}
