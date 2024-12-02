package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.topics.comment}")
    private String commentTopic;

    @Value(value = "${spring.kafka.topics.posts}")
    private String postsTopic;

    @Value(value = "${spring.kafka.topics.post-view}")
    private String postViewTopic;

    @Value(value = "${spring.kafka.topics.cache-heat}")
    private String cacheHeatTopic;

    @Value(value = "${spring.kafka.topics.like}")
    private String likeTopic;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.topics.comment-partitions-num}")
    private int partitionsNum;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentKafkaTopic() {
        return new NewTopic(commentTopic, partitionsNum, (short) 1);
    }

    @Bean
    public NewTopic postKafkaTopic() {
        return new NewTopic(postsTopic, partitionsNum, (short) 1);
    }

    @Bean
    public NewTopic postViewKafkaTopic() {
        return new NewTopic(postViewTopic, partitionsNum, (short) 1);
    }

    @Bean
    public NewTopic cacheHeatKafkaTopic() {
        return new NewTopic(cacheHeatTopic, partitionsNum, (short) 1);
    }

    @Bean
    public NewTopic likeKafkaTopic() {
        return new NewTopic(likeTopic, partitionsNum, (short) 1);
    }
}
