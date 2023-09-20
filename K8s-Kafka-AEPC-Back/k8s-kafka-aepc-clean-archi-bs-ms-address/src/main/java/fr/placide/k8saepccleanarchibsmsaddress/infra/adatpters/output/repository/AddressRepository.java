package fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.repository;

import fr.placide.k8saepccleanarchibsmsaddress.infra.adatpters.output.models.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AddressRepository extends JpaRepository<AddressModel, String> {
    List<AddressModel> findByNumAndStreetAndPoBoxAndCityAndCountry(
            int num, String street, int poBox, String city, String country);
    List<AddressModel> findByCity(String city);
}
