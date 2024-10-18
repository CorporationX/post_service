package faang.school.postservice.service.consumer;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redisCache.RedisFeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaPostConsumer {

  private final PostRepository postRepository;
  private final RedisFeedRepository redisFeedRepository;
  private final int limit;
  private final int acknowledgmentNack;

  public KafkaPostConsumer(PostRepository postRepository,
                           RedisFeedRepository redisFeedRepository,
                           @Value("${spring.data.redis.feed.limit:20}") int limit,
                           @Value("${spring.data.kafka.acknowledgment_nack_sec:5}") int acknowledgmentNack) {
    this.postRepository = postRepository;
    this.redisFeedRepository = redisFeedRepository;
    this.limit = limit;
    this.acknowledgmentNack = acknowledgmentNack;
  }

  @KafkaListener(topics = "${spring.data.kafka.topics.post.name}", groupId = "${spring.data.kafka.topics.post.consumer_group}")
  public void listenerPostEvent(PostEvent event, Acknowledgment acknowledgment) {

    try {
      for (Long subscriberId : event.getSubscriberIds()) {
        Optional<List<Long>> optionalFeed = redisFeedRepository.getAllFeed(subscriberId);

        if (optionalFeed.isEmpty()) {
          Map<Long, Long> postIdsTimestamp = postRepository.findByAuthorIdLimitAndOrder(event.getAuthorId(), limit)
                  .stream()
                  .collect(Collectors.toMap(
                          Post::getId,
                          post -> post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
                  ));

          redisFeedRepository.saveFeed(subscriberId, postIdsTimestamp);
          break;
        }

        switch (event.getEventType()) {
          case PUBLISHED -> {
            redisFeedRepository.addPostToFeed(subscriberId, event.getPostId());
            redisFeedRepository.trimFeed(subscriberId, limit);
          }
          case DELETE -> redisFeedRepository.deletePostToFeed(subscriberId, event.getPostId());
        }
      }

      acknowledgment.acknowledge();

    } catch (Exception e) {
      log.error("Error processing PostEvent: {}", event, e);
      acknowledgment.nack(Duration.ofSeconds(acknowledgmentNack));
    }
  }
}
