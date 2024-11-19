package faang.school.postservice.config.kafka.topic;

import faang.school.postservice.config.kafka.topic.params.KafkaCommentTopicParams;
import faang.school.postservice.config.kafka.topic.params.KafkaLikeTopicParams;
import faang.school.postservice.config.kafka.topic.params.KafkaPostTopicParams;
import faang.school.postservice.config.kafka.topic.params.KafkaPostViewTopicParams;
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

    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postKafkaTopic(KafkaPostTopicParams params) {
        return new NewTopic(params.getName(), params.getPartitions(), params.getReplicationFactor());
    }

    @Bean
    public NewTopic likeKafkaTopic(KafkaLikeTopicParams params) {
        return new NewTopic(params.getName(), params.getPartitions(), params.getReplicationFactor());
    }

    @Bean
    public NewTopic commentKafkaTopic(KafkaCommentTopicParams params) {
        return new NewTopic(params.getName(), params.getPartitions(), params.getReplicationFactor());
    }

    @Bean
    public NewTopic postViewKafkaTopic(KafkaPostViewTopicParams params) {
        return new NewTopic(params.getName(), params.getPartitions(), params.getReplicationFactor());
    }
}
