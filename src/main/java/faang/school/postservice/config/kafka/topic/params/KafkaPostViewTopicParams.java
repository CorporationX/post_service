package faang.school.postservice.config.kafka.topic.params;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topic.post-view-topic")
public class KafkaPostViewTopicParams extends AbstractKafkaTopicParams{
}
