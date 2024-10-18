package faang.school.postservice.service.publisher.messagePublishers;

import faang.school.postservice.service.publisher.MessagePublisher;
import org.springframework.data.redis.core.RedisTemplate;

public class LikeEventPublisher extends MessagePublisher {

  public LikeEventPublisher(RedisTemplate<String, Object> template,
                            String topicName) {
    super(template, topicName);
  }
}
