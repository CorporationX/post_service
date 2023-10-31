package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.data.kafka.groupId}")
    private int groupId;
    @Value("${spring.data.kafka.groupId}")
    private int topic;
    @Value("${spring.data.kafka.topics.commentTopic.name}")
    private String commentTopicName;
    @Value("${spring.data.kafka.topics.postTopic.name}")
    private String postTopicName;
    @Value("${spring.data.kafka.topics.likeTopic.name}")
    private String likeTopicName;
    @Value("${spring.data.kafka.topics.postViewTopic.name}")
    private String postViewTopicName;
    @Value("${spring.data.kafka.topics.feedHeatTopic.name}")
    private String feedHeatTopicName;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopicName, null, null);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postTopicName, null, null);
    }

    @Bean
    public NewTopic likeTopic() {
        return new NewTopic(likeTopicName, null, null);
    }

    @Bean
    public NewTopic viewTopic() {
        return new NewTopic(postViewTopicName, null, null);
    }

    @Bean
    public NewTopic feedHeatTopic() {
        return new NewTopic(feedHeatTopicName, null, null);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
