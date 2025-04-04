// KafkaConsumersConfig.java
package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Slf4j
@Configuration
public class KafkaConsumersConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.consumer.backoff.delay}")
    private long backoffDelay;

    @Value("${spring.kafka.consumer.backoff.max-attempts}")
    private long maxAttempts;

    @Bean
    public ConsumerFactory<String, FeedDto> feedDtoConsumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(FeedDto.class)
        );
    }

    @Bean
    public ConsumerFactory<String, PostDto> postDtoConsumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new JsonDeserializer<>(PostDto.class)
        );
    }

    @Bean(name = "feedDtoKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, FeedDto> feedDtoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FeedDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(feedDtoConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean(name = "postDtoKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, PostDto> postDtoKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(postDtoConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        FixedBackOff backOff = new FixedBackOff(backoffDelay, maxAttempts);
        DefaultErrorHandler handler = new DefaultErrorHandler(backOff);
        handler.setRetryListeners((record, ex, attempt) ->
                log.error("Retry #{} failed for record: {}", attempt, record)
        );
        return handler;
    }
}