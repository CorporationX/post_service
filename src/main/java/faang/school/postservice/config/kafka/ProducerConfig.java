package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.producer.EventProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ProducerConfig {

  @Bean
  public EventProducer<PostEvent> postProducer(KafkaTemplate<String, Object> template,
                                               NewTopic postTopic) {
    return new EventProducer<>(template, postTopic);
  }

  @Bean
  public EventProducer<PostViewEvent> postViewProducer(KafkaTemplate<String, Object> template,
                                                       NewTopic postViewTopic) {
    return new EventProducer<>(template, postViewTopic);
  }

  @Bean
  public EventProducer<LikeEvent> likeEventEventProducer(KafkaTemplate<String, Object> template,
                                                         NewTopic likeTopic) {
    return new EventProducer<>(template, likeTopic);
  }

  @Bean
  public EventProducer<CommentEvent> commentProducer(KafkaTemplate<String, Object> template,
                                                     NewTopic commentTopic) {
    return new EventProducer<>(template, commentTopic);
  }

}
