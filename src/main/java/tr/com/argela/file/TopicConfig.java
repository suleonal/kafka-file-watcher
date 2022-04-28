package tr.com.argela.file;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

    @Autowired
    KafkaConfig config;

    /*@Bean
    public NewTopic topic1() {
        return TopicBuilder.name(config.getTopicName())
                .partitions()
                .replicas(1)
                .build();
    }*/

}
