package fr.placide.k8skafkaaepccleanarchibsmscompany.domain.usecase;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Type;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;

public class Validator {
    private Validator(){}
    public static boolean areValidCompanyFields(String name, String agency, String type){
        return !name.isBlank()
                 && !agency.isBlank()
                 && !type.isBlank();
    }
    public static boolean checkTypeExists(String type){
        boolean exists = false;
        for(Type it: Type.values()){
            if(type.equals(it.getCompanyType())){
                exists = true;
                break;
            }
        }
        return exists;
    }
    public static void format(CompanyDto dto){
        dto.setName(dto.getName().strip().toUpperCase());
        dto.setAgency(dto.getAgency().strip());
        dto.setType(dto.getType().strip());
    }
}
