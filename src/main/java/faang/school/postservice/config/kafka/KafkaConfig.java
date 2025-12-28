package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.event.UserBanEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.trusted-packages}")
    private String trustedPackages;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.ack-mode}")
    private ContainerProperties.AckMode ackMode;

    @Bean
    public ProducerFactory<String, Object> stringObjectProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps(StringSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, Object> stringObjectKafkaTemplate() {
        return new KafkaTemplate<>(stringObjectProducerFactory());
    }

    @Bean
    public ProducerFactory<Long, CommentEventDto> longCommentEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps(LongSerializer.class));
    }

    @Bean
    public KafkaTemplate<Long, CommentEventDto> longCommentEventKafkaTemplate() {
        return new KafkaTemplate<>(longCommentEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, UserBanEvent> userBanEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps(StringSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, UserBanEvent> userBanEventKafkaTemplate() {
        return new KafkaTemplate<>(userBanEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, PostPublishEventDto> postEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps(StringSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, PostPublishEventDto> postEventKafkaTemplate() {
        return new KafkaTemplate<>(postEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, LikeEventDto> likeEventProducerFactory() {
        return new DefaultKafkaProducerFactory<>(getConfigProps(StringSerializer.class));
    }

    @Bean
    public KafkaTemplate<String, LikeEventDto> likeEventKafkaTemplate() {
        return new KafkaTemplate<>(likeEventProducerFactory());
    }

    private Map<String, Object> getConfigProps(Class<?> keySerializerClass) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializerClass);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return configProps;
    }

    private Map<String, Object> getConsumerConfigProps(String groupId) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        return configProps;
    }

    @Bean
    public ConsumerFactory<String, PostPublishEventDto> postPublishEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfigProps(groupId));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostPublishEventDto> concurrentKafkaPostListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostPublishEventDto> concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(postPublishEventConsumerFactory());
        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ackMode);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    public ConsumerFactory<String, LikeEventDto> likeEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfigProps(groupId));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LikeEventDto> concurrentKafkaLikeListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LikeEventDto> concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(likeEventConsumerFactory());
        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ackMode);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean
    public ConsumerFactory<String, CommentEventDto> commentEventConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getConsumerConfigProps(groupId));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommentEventDto> concurrentKafkaCommentListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CommentEventDto> concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(commentEventConsumerFactory());
        concurrentKafkaListenerContainerFactory.getContainerProperties().setAckMode(ackMode);
        return concurrentKafkaListenerContainerFactory;
    }
}