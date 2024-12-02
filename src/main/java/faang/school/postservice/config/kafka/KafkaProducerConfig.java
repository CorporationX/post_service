package faang.school.postservice.config.kafka;

import faang.school.postservice.util.KafkaProducerConstants;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {
    private final KafkaProducerConstants kafkaProduceConstants;

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> multiTypeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS, kafkaProduceConstants.getCOMMENT_EVENT()
                + kafkaProduceConstants.getPOST_EVENT()
                + kafkaProduceConstants.getPOST_VIEW_EVENT()
                + kafkaProduceConstants.getHEAT_TASK_EVEN()
                + kafkaProduceConstants.getLIKE_EVENT());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> multiTypeKafkaTemplate() {
        return new KafkaTemplate<>(multiTypeProducerFactory());
    }
}
