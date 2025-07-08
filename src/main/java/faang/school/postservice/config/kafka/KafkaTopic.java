package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Value("${spring.data.kafka.topic.comment.name}")
    private String commentTopic;

    @Value("${spring.data.kafka.topic.comment.partitions}")
    private int commentPartitions;

    @Value("${spring.data.kafka.topic.comment.replicas}")
    private int commentReplicas;

    @Value("${spring.data.kafka.topic.userBan}")
    private String userBanTopic;


    @Bean
    public NewTopic userBan() {
        return TopicBuilder.name(userBanTopic).partitions(1).build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(commentTopic).partitions(commentPartitions).replicas(commentReplicas).build();
    }
}

