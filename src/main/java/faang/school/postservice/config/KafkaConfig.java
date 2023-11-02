package faang.school.postservice.config;

import faang.school.postservice.dto.client.UserDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.topics.feed-topic.name}")
    private String feedTopicName;
    @Value("${spring.kafka.topics.like-topic.name}")
    private String likeTopicName;
    @Value("${spring.kafka.topics.comment-topic.name}")
    private String commentTopicName;
    @Value("${spring.kafka.topics.feed-topic.partitions}")
    private int feedPartitionCount;
    @Value("${spring.kafka.topics.feed-topic.replicas}")
    private int feedReplicaCount;
    @Value("${spring.kafka.topics.like-topic.partitions}")
    private int likePartitionCount;
    @Value("${spring.kafka.topics.like-topic.replicas}")
    private int likeReplicaCount;
    @Value("${spring.kafka.topics.comment-topic.partitions}")
    private int commentPartitionCount;
    @Value("${spring.kafka.topics.comment-topic.replicas}")
    private int commentReplicaCount;

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }


    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic topicFeed() {
        return TopicBuilder.name(feedTopicName)
                .partitions(feedPartitionCount)
                .replicas(feedReplicaCount)
                .build();
    }

    @Bean
    public NewTopic topicLike() {
        return TopicBuilder.name(likeTopicName)
                .partitions(likePartitionCount)
                .replicas(likePartitionCount)
                .build();
    }

    @Bean
    public NewTopic topicComment() {
        return TopicBuilder.name(commentTopicName)
                .partitions(commentPartitionCount)
                .replicas(commentReplicaCount)
                .build();
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory
            (KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(kafkaProperties));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserDto> consumerUserFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(), new JsonDeserializer<>(UserDto.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDto> kafkaListenerUserContainerFactory
            (KafkaProperties kafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<String, UserDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerUserFactory(kafkaProperties));
        return factory;
    }
}
