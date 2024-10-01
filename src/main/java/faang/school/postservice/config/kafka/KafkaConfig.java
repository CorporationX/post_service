package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topic.posts.name}")
    private String topicPostName;
    @Value("${spring.kafka.topic.comments.name}")
    private String topicCommentName;
    @Value("${spring.kafka.topic.post-views.name}")
    private String topicPostViewsName;
    @Value("${spring.kafka.topic.like.name}")
    private String topicLikeName;
    @Value("${spring.kafka.topic.partitions}")
    private int partitions;
    @Value("${spring.kafka.topic.replication-factor}")
    private short replications;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(topicPostName, partitions, replications);
    }

    @Bean
    public NewTopic commentsTopic() {
        return new NewTopic(topicCommentName, partitions, replications);
    }

    @Bean
    public NewTopic postViewsTopic(){
        return new NewTopic(topicPostViewsName, partitions, replications);
    }

    @Bean
    public NewTopic likeTopic(){
        return new NewTopic(topicLikeName, partitions,replications);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
