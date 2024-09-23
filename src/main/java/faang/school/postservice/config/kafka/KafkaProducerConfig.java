package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.data.kafka.bootstrap-servers}")
    public String bootstrapAddress;

    @Value("${spring.data.kafka.topics.post_topic}")
    private String postTopic;

    @Value("${spring.data.kafka.topics.like_topic}")
    private String likeTopic;

    @Value("${spring.data.kafka.topics.comment_topic}")
    private String commentTopic;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic postKafkaTopic() {
        return new NewTopic(postTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic likeKafkaTopic() {
        return new NewTopic(likeTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic commentKafkaTopic() {
        return new NewTopic(commentTopic, 1, (short) 1);
    }
}
