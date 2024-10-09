package faang.school.postservice.config.kafka;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.ack}")
    private String ack;
    @Value("${spring.kafka.producer.buffer-memory}")
    private long bufferMemory;
    @Value("${spring.kafka.producer.batch-size}")
    private int butchSize;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.ACKS_CONFIG, ack);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, butchSize);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public NewTopic postTheme() {
        return new NewTopic("post_theme", 3, (short) 1);
    }

    @Bean
    public NewTopic commentTheme() {
        return new NewTopic("comment_theme", 3, (short) 1);
    }

    @Bean
    public NewTopic likeTheme() {
        return new NewTopic("like_theme", 3, (short) 1);
    }

    @Bean
    public NewTopic viewTheme(){
        return new NewTopic("view_theme", 3,(short) 1);
    }

    @Bean
    public NewTopic subsTheme(){
        return new NewTopic("subs_theme", 3,(short) 1);
    }
}
