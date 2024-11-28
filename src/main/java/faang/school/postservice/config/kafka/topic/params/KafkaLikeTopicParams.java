package faang.school.postservice.config.kafka.topic.params;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.like-topic")
public class KafkaLikeTopicParams extends AbstractKafkaTopicParams {
}
