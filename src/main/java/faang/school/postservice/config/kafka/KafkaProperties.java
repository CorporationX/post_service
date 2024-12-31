package faang.school.postservice.config.kafka;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaProperties {
    @Value("${spring.data.kafka.consumer.group-id}")
    private String configGroupId;

    @Value("${spring.data.kafka.consumer.enable_auto_commit}")
    private String consumerAutoCommit;

    @Value("${spring.data.kafka.producer.acks}")
    private String producerAcks;

    @Value("${spring.data.kafka.producer.retries}")
    private Integer producerRetries;

    @Value("${spring.data.kafka.listener.ack_mode}")
    private String listenerAcksMode;

    @Value("${spring.data.kafka.bootstrap_servers.server}")
    private String bootstrapServer;

    @Value("${spring.data.kafka.topic_name.posts}")
    private String postsTopic;

    @Value("${spring.data.kafka.topic_name.post_views}")
    private String postViewsTopic;

    @Value("${spring.data.kafka.topic_name.comments}")
    private String commentsTopic;

    @Value("${spring.data.kafka.topic_name.heat_feed}")
    private String heatFeedsTopic;

    @Value("${spring.data.kafka.topic_name.heat_posts}")
    private String heatPostsTopic;

    @Value("${spring.data.kafka.topics_param.replicas}")
    private Integer replica;

    @Value("${spring.data.kafka.topics_param.partitions}")
    private Integer partition;

    @Value("${spring.data.kafka.backoff.interval}")
    private Long backoffInterval;

    @Value("${spring.data.kafka.backoff.max_attempt}")
    private Long backoffMaxAttempt;
}