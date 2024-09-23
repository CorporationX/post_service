package faang.school.postservice.config.kafka;

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
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServerAddress;

    @Value("${spring.kafka.topic-name.posts}")
    private String postTopicName;

    @Value("${spring.kafka.topic-name.likes}")
    private String likeTopicName;

    @Value("${spring.kafka.topic-name.comments}")
    private String commentTopicName;

    @Value("${spring.kafka.topic-name.post-views}")
    private String postViewTopicName;

    @Value("${spring.kafka.producer.idempotence}")
    private boolean producerIdempotence;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServerAddress);
        configs.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerIdempotence);
        configs.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return TopicBuilder.name(postTopicName).build();
    }

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopicName).build();
    }

    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder.name(commentTopicName).build();
    }

    @Bean
    public NewTopic postViewTopic() {
        return TopicBuilder.name(postTopicName).build();
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}