package fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.repository;

import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProjectRepository extends JpaRepository<ProjectModel, String> {
    List<ProjectModel> findByNameAndDescriptionAndStateAndEmployeeIdAndCompanyId(
            String name, String description, String state,String employeeId, String companyId);
    List<ProjectModel> findByEmployeeId(String employeeId);
    List<ProjectModel> findByCompanyId(String companyId);
}
