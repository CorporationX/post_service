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
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    private final String bootstrapAddress;
    private final String postTopic;
    private final String likeTopic;
    private final String commentTopic;
    private final String viewsTopic;


    public KafkaProducerConfig(@Value("${spring.data.kafka.bootstrap}") String bootstrapAddress,
                               @Value("${spring.data.kafka.topic-name.posts}") String postTopic,
                               @Value("${spring.data.kafka.topic-name.likes}") String likesTopic,
                               @Value("${spring.data.kafka.topic-name.comments}") String commentsTopic,
                               @Value("${spring.data.kafka.topic-name.views}") String viewsTopic) {

        this.bootstrapAddress = bootstrapAddress;
        this.postTopic = postTopic;
        this.likeTopic = likesTopic;
        this.commentTopic = commentsTopic;
        this.viewsTopic = viewsTopic;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
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

    @Bean
    public NewTopic viewKafkaTopic() {
        return new NewTopic(viewsTopic, 1, (short) 1);
    }
}
