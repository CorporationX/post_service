package faang.school.postservice.service.consumer;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.redisCache.PostCache;
import faang.school.postservice.repository.redisCache.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
  private final RedisPostRepository redisPostRepository;
  private final RedisTemplate<String, Object> redisTemplate;

  @KafkaListener(topics = "${spring.data.kafka.topics.comment.name}")
  public void listenerCommentEvent(CommentEvent event, Acknowledgment acknowledgment) {
    Long postId = event.getPostId();
    // TODO: повтор
    redisTemplate.watch("Posts:" + postId);

    try {
      redisPostRepository.findById(postId)
              .ifPresent(postCache -> {
                processEvent(event, postCache);

                redisTemplate.multi();
                redisPostRepository.save(postCache);

                if (redisTemplate.exec() != null) {
                  acknowledgment.acknowledge();
                }
                ;
              });
    } finally {
      redisTemplate.unwatch();
    }
  }

  public void processEvent(CommentEvent event, PostCache postCache) {
    switch (event.getEventType()) {
      case CREATE -> {
        Deque<Long> lastCommentIds = new ArrayDeque<>(postCache.getLastCommentIds());
        if (lastCommentIds.size() > 2) {
          lastCommentIds.removeLast();
        }
        lastCommentIds.addFirst(event.getId());
      }
      case DELETE -> postCache.getLastCommentIds().remove(event.getId());
    }
  }
}
