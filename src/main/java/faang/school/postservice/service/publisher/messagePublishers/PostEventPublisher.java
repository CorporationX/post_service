package faang.school.postservice.service.publisher.messagePublishers;

import faang.school.postservice.service.publisher.MessagePublisher;
import org.springframework.data.redis.core.RedisTemplate;

public class PostEventPublisher extends MessagePublisher {

  public PostEventPublisher(RedisTemplate<String, Object> template,
                            String topicName) {
    super(template, topicName);
  }
}
