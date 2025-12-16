package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.kafka.PostEvent;
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

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${kafka.bootstrap_servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, PostEvent> postKafkaProducerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        JsonSerializer<PostEvent> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(false);
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, PostEvent> postKafkaTemplate(
            ProducerFactory<String, PostEvent> postKafkaProducerFactory
    ) {
        return new KafkaTemplate<>(postKafkaProducerFactory);
    }
}
