package faang.school.postservice.config;

import faang.school.postservice.dto.like.LikeDto;
import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Getter
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;
    private final String postLikeTopicName = "PostLike";

    @Bean
    public ProducerFactory<String, LikeDto> producerFactory() {
        Map<String, Object> config = new HashMap();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public NewTopic taskTopicPostLike() {
        return TopicBuilder.name("PostLike")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public KafkaTemplate<String, LikeDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
