package fr.placide.k8skafkaaepccleanarchibsmsproject.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.Priority;
import fr.placide.k8skafkaaepccleanarchibsmsproject.domain.beans.project.State;
import fr.placide.k8skafkaaepccleanarchibsmsproject.infra.adapters.output.models.ProjectDto;

import static fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.Msg.REMOTE_COMPANY_API_UNREACHABLE;
import static fr.placide.k8skafkaaepccleanarchibsmsproject.domain.exceptions.Msg.REMOTE_EMPLOYEE_API_UNREACHABLE;

public class Validator {
    private Validator(){}
    public static boolean isValidProject(String name, String desc, String employeeId, String companyId){
        return !name.isBlank()
                && !desc.isBlank()
                && !employeeId.isBlank()
                && !companyId.isBlank();
    }
    public static boolean isValidProject(int priority){
        boolean valid = false;
        for(Priority it : Priority.values()){
            if(priority==it.getProjectPriority()){
                valid=true;
                break;
            }
        }
        return valid;
    }
    public static boolean isValidProject(String state){
        boolean valid = false;
        for(State it : State.values()){
            if(state.equals(it.getProjectState())){
                valid=true;
                break;
            }
        }
        return valid;
    }
    public static boolean remoteEmployeeApiUnreachable(String employeeId){
        return employeeId.strip().equals(REMOTE_EMPLOYEE_API_UNREACHABLE);
    }
    public static boolean remoteCompanyApiUnreachable(String companyId){
        return companyId.strip().equals(REMOTE_COMPANY_API_UNREACHABLE);
    }
    public static void format(ProjectDto dto){
        dto.setName(dto.getName().strip().toUpperCase());
        dto.setDescription(dto.getDescription().strip());
        dto.setState(dto.getState().strip());
        dto.setEmployeeId(dto.getEmployeeId().strip());
        dto.setCompanyId(dto.getCompanyId().strip());
    }
}
