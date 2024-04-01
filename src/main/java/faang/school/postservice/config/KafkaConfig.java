package faang.school.postservice.config;

import nonapi.io.github.classgraph.json.JSONSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${kafka.topic.post_topic}")
    private String postTopic;
    @Value("${kafka.topic.post_views_topic}")
    private String postViewTopic;
    @Value("${kafka.topic.post_likes_topic}")
    private String postLikeTopic;
    @Value("${kafka.topic.post_comments_topic}")
    private String postCommentTopic;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JSONSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JSONSerializer.class);
        properties.put(ProducerConfig.ACKS_CONFIG, 1);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JSONSerializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JSONSerializer.class);

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postTopic, 10, (short) 2);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic(postViewTopic, 10, (short) 2);
    }

    @Bean
    public NewTopic postLikeTopic() {
        return new NewTopic(postLikeTopic, 10, (short) 2);
    }

    @Bean
    public NewTopic postCommentTopic() {
        return new NewTopic(postCommentTopic, 10, (short) 2);
    }


}
