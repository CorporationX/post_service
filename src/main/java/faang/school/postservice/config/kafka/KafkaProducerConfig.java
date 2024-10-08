package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
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
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap_servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.acks:1}")
    private String acks;
    @Value("${spring.kafka.producer.retries:10}")
    private int retries;

    @Bean
    public Map<String, Object> producerConfig(){
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configs.put(ProducerConfig.ACKS_CONFIG, acks);
        configs.put(ProducerConfig.RETRIES_CONFIG, retries);
        return configs;
    }
    @Bean
    public ProducerFactory<String, Object> producerFactory(){
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}
