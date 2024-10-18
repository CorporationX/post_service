package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {

  @Bean
  public NewTopic postViewTopic(@Value("${spring.data.kafka.topics.post_view.name}") String topicName) {
    return TopicBuilder.name(topicName)
            .partitions(1)
            .replicas(1)
            .build();
  }

  @Bean
  public NewTopic commentTopic(@Value("${spring.data.kafka.topics.comment.name}") String topicName) {
    return TopicBuilder.name(topicName)
            .partitions(1)
            .replicas(1)
            .build();
  }

  @Bean
  public NewTopic likeTopic(@Value("${spring.data.kafka.topics.like.name}") String topicName) {
    return TopicBuilder.name(topicName)
            .partitions(1)
            .replicas(1)
            .build();
  }

  @Bean
  public NewTopic postTopic(@Value("${spring.data.kafka.topics.post.name}") String topicName) {
    return TopicBuilder.name(topicName)
            .partitions(1)
            .replicas(1)
            .build();
  }


}
