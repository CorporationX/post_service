package faang.school.postservice.config;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServers;

    @Bean
    public ConsumerFactory<String, LikeDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getCommonConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LikeDto> kafkaListenerContainerFactory (
            ConsumerFactory<String, LikeDto> consumerFactoryLike) {
        return createContainerFactory(consumerFactoryLike);
    }

    @Bean
    public ConsumerFactory<String, PostAndFollowersDto> postAndFollowersConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getCommonConfig());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostAndFollowersDto> postAndFollowersKafkaListenerContainerFactory(
            ConsumerFactory<String, PostAndFollowersDto> postAndFollowersConsumerFactory) {
        return createContainerFactory(postAndFollowersConsumerFactory);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> createContainerFactory(
            ConsumerFactory<String, T> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }

    private Map<String, Object> getCommonConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put("spring.json.type.mapping",
                "faang.school.postservice.dto.like.LikeDto:faang.school.postservice.dto.like.LikeDto," +
                "faang.school.postservice.dto.post.PostDto:faang.school.postservice.dto.post.PostDto," +
                "faang.school.postservice.dto.post.PostAndFollowersDto:faang.school.postservice.dto.post.PostAndFollowersDto");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "post-service-instance-1");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "post-service-consumer-1");
        return config;
    }
}
