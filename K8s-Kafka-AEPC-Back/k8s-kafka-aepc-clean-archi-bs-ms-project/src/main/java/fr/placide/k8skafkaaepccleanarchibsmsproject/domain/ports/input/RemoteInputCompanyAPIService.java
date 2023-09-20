package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.input;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;

import java.util.Optional;

public interface RemoteInputCompanyAPIService {
    Optional<Company> getRemoteCompanyAPI(String companyId) throws RemoteCompanyApiException;
}
