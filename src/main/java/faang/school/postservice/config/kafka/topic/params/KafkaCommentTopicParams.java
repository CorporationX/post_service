package faang.school.postservice.config.kafka.topic.params;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.comment-topic")
public class KafkaCommentTopicParams extends AbstractKafkaTopicParams{
}
