package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.consumer.trustedPackage}")
    private String trustedPackage;
    @Value("${spring.kafka.consumer.properties.auto.offset}")
    private String autoOffset;

    @Bean(name = "consumerFactory")
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages(trustedPackage);
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffset);
        return new DefaultKafkaConsumerFactory<>(configProperties, new StringDeserializer(), deserializer);
    }

    @Bean(name = "objectContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Object> objectContainerFactory(
            @Qualifier(value = "consumerFactory") ConsumerFactory<String, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}