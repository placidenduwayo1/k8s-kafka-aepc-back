package fr.placide.k8skafkaaepccleanarchibsmscompany.infra.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicsCreation {
    @Value("${topics.names.topic1}")
    private String topic1;
    @Value("${topics.names.topic2}")
    private String topic2;
    @Value("${topics.names.topic3}")
    private String topic3;
    private static final int NB_PARTITIONS = 1;

    @Bean
    public NewTopic createTopicCompanyAdd() {
        return TopicBuilder
                .name(topic1)
                .partitions(NB_PARTITIONS)
                .build();
    }

    @Bean
    public NewTopic createTopicCompanyDelete() {
        return TopicBuilder
                .name(topic2)
                .partitions(NB_PARTITIONS)
                .build();
    }

    @Bean
    public NewTopic createTopicCompanyEdit() {
        return TopicBuilder
                .name(topic3)
                .partitions(NB_PARTITIONS)
                .build();
    }
}
