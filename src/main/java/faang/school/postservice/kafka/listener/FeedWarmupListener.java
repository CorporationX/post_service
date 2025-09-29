package faang.school.postservice.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.cache.feed.FeedWarmupProperties;
import faang.school.postservice.dto.user.feed.CacheWarmupTask;
import faang.school.postservice.dto.user.feed.HeatUserTask;
import faang.school.postservice.service.feed.warmup.FeedWarmupServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedWarmupListener {

    private final ObjectMapper objectMapper;
    private final FeedWarmupServiceImpl feedWarmupServiceImpl;
    private final FeedWarmupProperties feedWarmupProperties;

    @KafkaListener(
            topics = "${feed.kafka.topics.warmer}",
            concurrency = "${feed.cache.warm-up.concurrency}"
    )
    public void listen(String rawMessage) {
        try {
            CacheWarmupTask taskBatch = objectMapper.readValue(rawMessage, CacheWarmupTask.class);
            int limit = feedWarmupProperties.limit();

            List<Long> userIds = taskBatch.userIds();
            if (userIds == null) {
                userIds = List.of();
            }

            userIds.stream()
                    .filter(Objects::nonNull)
                    .forEach(userId -> feedWarmupServiceImpl.warmUpUser(new HeatUserTask(userId, limit)));

        } catch (Exception e) {
            log.error("FeedWarmupListener: failed to process batch. payload={}", rawMessage, e);
        }
    }
}