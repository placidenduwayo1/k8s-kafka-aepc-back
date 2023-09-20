package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.services;

import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.ports.output.OutputKafkaProducerService;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.bean.Company;
import fr.placide.k8skafkaaepccleanarchibsmscompany.domain.exceptions.CompanyNotFoundException;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.mapper.CompanyMapper;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyDto;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.models.CompanyModel;
import fr.placide.k8skafkaaepccleanarchibsmscompany.infra.adapters.output.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaProducerCompanyEventService implements OutputKafkaProducerService {
    private final KafkaTemplate<String, Company> companyKafkaTemplate;
    private final CompanyRepository repository;
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;

    public KafkaProducerCompanyEventService(KafkaTemplate<String, Company> companyKafkaTemplate, CompanyRepository repository) {
        this.companyKafkaTemplate = companyKafkaTemplate;
        this.repository = repository;
    }

    private Message<?> buildMessage(Company payload, String topic){
        return MessageBuilder.withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
    }

    @Override
    public Company produceKafkaEventCompanyCreate(Company company) {
        Message<?> message = buildMessage(company,topic1);
        companyKafkaTemplate.send(message);
        return company;
    }

    @Override
    public Company produceKafkaEventCompanyDelete(String id) throws CompanyNotFoundException {
        CompanyModel model = repository.findById(id).orElseThrow(CompanyNotFoundException::new);
        Company bean = CompanyMapper.fromModelToBean(model);
        Message<?> message =buildMessage(bean,topic2);
        companyKafkaTemplate.send(message);
        return bean;
    }

    @Override
    public Company produceKafkaEventCompanyEdit(CompanyDto payload, String id) throws CompanyNotFoundException {
        Optional<CompanyModel> model = repository.findById(id);
        if(model.isEmpty()){
            throw new CompanyNotFoundException();
        }
        Company bean = CompanyMapper.fromDtoToBean(payload);
        bean.setConnectedDate(model.get().getConnectedDate());
        Message<?> message = buildMessage(bean,topic3);
        companyKafkaTemplate.send(message);
        return bean;
    }
}
