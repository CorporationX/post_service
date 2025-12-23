package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.topics.like_topic}")
    private String likeTopicName;

    @Bean
    public ProducerFactory<String, Object> postKafkaProducerFactory() {
        Map<String, Object> configProperties = new HashMap<>();
        JsonSerializer<Object> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(false);
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new DefaultKafkaProducerFactory<>(configProperties, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, Object> postKafkaTemplate(
            ProducerFactory<String, Object> postKafkaProducerFactory
    ) {
        return new KafkaTemplate<>(postKafkaProducerFactory);
    }

    @Bean
    public NewTopic kafkaLikeTopic() {
        return new NewTopic(likeTopicName, 1, (short) 1);
    }
}
