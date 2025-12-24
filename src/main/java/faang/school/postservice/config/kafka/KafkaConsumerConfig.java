package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.event.KafkaEvent;
import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, LikeEvent> likeConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "post-service-like-consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<LikeEvent> deserializer =
                new JsonDeserializer<>(LikeEvent.class);
        deserializer.addTrustedPackages("faang.school.postservice.event");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LikeEvent>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LikeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(likeConsumerFactory());
        return factory;
    }
}


//@EnableKafka
//@Configuration
//@RequiredArgsConstructor
//public class KafkaConsumerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Bean
//    public ConsumerFactory<String, KafkaEvent> postConsumerFactory() {
//        Map<String, Object> configProperties = new HashMap<>();
//
//        JsonDeserializer<KafkaEvent> deserializer =
//                new JsonDeserializer<>(KafkaEvent.class, false);
//
//        deserializer.addTrustedPackages("faang.school.postservice.event");
//        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        configProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//
//        return new DefaultKafkaConsumerFactory<>(configProperties, new StringDeserializer(), deserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> postContainerFactory(
//            ConsumerFactory<String, KafkaEvent> postConsumerFactory
//    ) {
//        ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> container =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        container.setConcurrency(1);
//        container.setConsumerFactory(postConsumerFactory);
//        container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//        return container;
//    }
//}

//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//
//    @Bean
//    public ConsumerFactory<String, Object> kafkaConsumerFactory(ObjectMapper objectMapper) {
//        Map<String, Object> props = new HashMap<>();
//
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "faang.school.postservice.event");
//        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
//        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE,
//                "faang.school.postservice.event.LikeEvent");
//
//        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
//                new JsonDeserializer<>(Object.class, objectMapper, false));
//    }
//
//    @Bean
//    public RecordMessageConverter messageConverter() {
//        return new JsonMessageConverter();
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object>
//    kafkaListenerContainerFactory(ConsumerFactory<String, Object> kafkaConsumerFactory) {
//
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//
//        factory.setConsumerFactory(kafkaConsumerFactory);
//        factory.setMessageConverter(messageConverter());
//        factory.getContainerProperties()
//                .setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//        return factory;
//    }
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return mapper;
//    }
//}
