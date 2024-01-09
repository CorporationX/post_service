package faang.school.postservice.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class Kafka {
    @Value("${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.data.kafka.topics.post-views.name}")
    private String postViewsTopic;
    @Value("${spring.data.kafka.topics.post-views.partitions}")
    private int postViewsPartitions;
    @Value("${spring.data.kafka.topics.post-views.replicas}")
    private int postViewReplicas;

    @Value("${spring.data.kafka.topics.likes.name}")
    private String likesTopic;
    @Value("${spring.data.kafka.topics.likes.partitions}")
    private int likesPartitions;
    @Value("${spring.data.kafka.topics.likes.replicas}")
    private int likesReplicas;

    @Value("${spring.data.kafka.topics.post.name}")
    private String postsTopic;
    @Value("${spring.data.kafka.topics.post.partitions}")
    private int postsPartitions;
    @Value("${spring.data.kafka.topics.post.replicas}")
    private int postsReplicas;

    @Value("${spring.data.kafka.topics.comments.name}")
    private String commentsTopic;
    @Value("${spring.data.kafka.topics.comments.partitions}")
    private int commentsPartitions;
    @Value("${spring.data.kafka.topics.comments.replicas}")
    private int commentsReplicas;

    @Value("${spring.data.kafka.topics.heater.name}")
    private String heaterTopic;
    @Value("${spring.data.kafka.topics.heater.partitions}")
    private int heaterPartitions;
    @Value("${spring.data.kafka.topics.heater.replicas}")
    private int heaterReplicas;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "post-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postViewsTopic() {
        return TopicBuilder.name(postViewsTopic)
                .partitions(postViewsPartitions)
                .replicas(postViewReplicas)
                .build();
    }

    @Bean
    public NewTopic likesTopic() {
        return TopicBuilder.name(likesTopic)
                .partitions(likesPartitions)
                .replicas(likesReplicas)
                .build();
    }

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name(postsTopic)
                .partitions(postsPartitions)
                .replicas(postsReplicas)
                .build();
    }

    @Bean
    public NewTopic commentsTopic() {
        return TopicBuilder.name(commentsTopic)
                .partitions(commentsPartitions)
                .replicas(commentsReplicas)
                .build();
    }

    @Bean
    public NewTopic heaterTopic() {
        return TopicBuilder.name(heaterTopic)
                .partitions(heaterPartitions)
                .replicas(heaterReplicas)
                .build();
    }
}
