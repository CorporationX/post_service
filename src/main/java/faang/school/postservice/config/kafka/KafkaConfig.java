package faang.school.postservice.config.kafka;

import faang.school.postservice.config.kafka.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class KafkaConfig {
    private final KafkaProperties properties;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configurationSource = new HashMap<>();
        configurationSource.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configurationSource.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.connection());
        configurationSource.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configurationSource);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configurationSource = new HashMap<>();
        configurationSource.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.connection());
        configurationSource.put(ConsumerConfig.GROUP_ID_CONFIG, properties.groupId());
        configurationSource.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.offset());
        configurationSource.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configurationSource.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(configurationSource);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        containerFactory.setConsumerFactory(consumerFactory);
        containerFactory.setConcurrency(properties.concurrency());
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return containerFactory;
    }

    @Bean
    public ExecutorService kafkaThreadPool() {
        return Executors.newFixedThreadPool(properties.poolSize());
    }
}
