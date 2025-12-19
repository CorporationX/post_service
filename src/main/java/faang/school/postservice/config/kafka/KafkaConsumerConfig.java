package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.kafka.PostEvent;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap_servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, PostEvent> postConsumerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        JsonDeserializer<PostEvent> deserializer =
                new JsonDeserializer<>(PostEvent.class, false);
        deserializer.addTrustedPackages("faang.school.postservice.dto.kafka");
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(configProperties, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PostEvent> postContainerFactory(
            ConsumerFactory<String, PostEvent> postConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, PostEvent> container =
                new ConcurrentKafkaListenerContainerFactory<>();
        container.setConcurrency(1);
        container.setConsumerFactory(postConsumerFactory);
        return container;
    }
}
