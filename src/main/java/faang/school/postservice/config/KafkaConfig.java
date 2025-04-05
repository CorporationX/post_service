package faang.school.postservice.config;

import faang.school.postservice.model.event.PostBySubscribersEvent;
import faang.school.postservice.properties.KafkaSettingsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    @Bean
    public ConsumerFactory<String, PostBySubscribersEvent> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(PostBySubscribersEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostBySubscribersEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PostBySubscribersEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }


    @Bean
    public NewTopic postEventsTopic(KafkaSettingsProperties kafkaSettingsProperties) {
        return TopicBuilder.name(kafkaSettingsProperties.getPostTopic())
                .partitions(kafkaSettingsProperties.getPartitions())
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic postLikeTopic(KafkaSettingsProperties kafkaSettingsProperties) {
        return TopicBuilder.name(kafkaSettingsProperties.getLikeTopic())
                .partitions(kafkaSettingsProperties.getPartitions())
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic postViewEventsTopic(KafkaSettingsProperties kafkaSettingsProperties) {
        return TopicBuilder.name(kafkaSettingsProperties.getPostViewTopic())
                .partitions(kafkaSettingsProperties.getPartitions())
                .replicas(1)
                .build();
    }
}
