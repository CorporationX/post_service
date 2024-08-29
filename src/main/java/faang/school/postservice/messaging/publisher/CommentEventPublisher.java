package faang.school.postservice.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.messaging.AbstractEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

  public CommentEventPublisher(ObjectMapper objectMapper,
      RedisTemplate<String, Object> redisTemplate,
      @Qualifier("commentTopic") ChannelTopic channelTopic) {
    super(objectMapper, redisTemplate, channelTopic);
  }

  public void publish(CommentEvent event) {
    publish(event);
  }
}
