package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.ports.output;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.company.Company;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.RemoteCompanyApiException;

import java.util.Optional;

public interface RemoteOutputCompanyAPIService {
    Optional<Company> getRemoteCompanyAPI(String companyId) throws RemoteCompanyApiException;
}
