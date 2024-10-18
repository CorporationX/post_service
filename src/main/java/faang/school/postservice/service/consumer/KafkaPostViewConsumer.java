package faang.school.postservice.service.consumer;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redisCache.RedisPostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaPostViewConsumer {

  private final PostRepository postRepository;
  private final RedisPostRepository redisPostRepository;
  private final RedisTemplate<String, Object> redisTemplate;
  private final int delay;
  private final int period;
  private final Map<Long, Integer> viewCounts = new HashMap<>();  // Хранит количество просмотров постов
  private final ScheduledExecutorService scheduler;

  public KafkaPostViewConsumer(PostRepository postRepository,
                               RedisPostRepository redisPostRepository, RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.kafka.topics.post_view.delay_minutes:5}") int delay,
                               @Value("${spring.data.kafka.topics.post_view.period_minutes:5}") int period,
                               @Value("${spring.data.kafka.topics.post_view.pool_size:1}") int poolSize) {
    this.postRepository = postRepository;
    this.redisPostRepository = redisPostRepository;
    this.redisTemplate = redisTemplate;
    this.delay = delay;
    this.period = period;
    scheduler = Executors.newScheduledThreadPool(poolSize);
    startBatchSaveScheduler();
  }

  @KafkaListener(topics = "${spring.data.kafka.topics.post_view.name}")
  public void listenerPostViewEvent(PostEvent event) {
    Long postId = event.getPostId();

    synchronized (viewCounts) {
      viewCounts.put(postId, viewCounts.getOrDefault(postId, 0) + 1);
    }
  }

  private void startBatchSaveScheduler() {
    scheduler.scheduleAtFixedRate(this::saveViewCountsToDatabase, delay, period, TimeUnit.MINUTES);
  }

  private void saveViewCountsToDatabase() {

    synchronized (viewCounts) {
      for (Map.Entry<Long, Integer> entry : viewCounts.entrySet()) {
        Long postId = entry.getKey();
        Integer count = entry.getValue();
        postRepository.updateNumberViews(postId, count);
        // TODO: повтор
        redisTemplate.watch("Posts:" + postId);

        try {
          redisPostRepository.findById(postId)
                  .ifPresent(postCache -> {
                    postCache.incNumberViews(count);

                    redisTemplate.multi();
                    redisPostRepository.save(postCache);
                  });
        } finally {
          redisTemplate.unwatch();
        }
      }

      viewCounts.clear();
    }
  }
}
