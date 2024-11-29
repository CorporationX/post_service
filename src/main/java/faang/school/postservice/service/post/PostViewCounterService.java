package faang.school.postservice.service.post;

import faang.school.postservice.publisher.kafka.KafkaEventProducer;
import faang.school.postservice.publisher.kafka.events.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewCounterService {
    private final KafkaEventProducer kafkaEventProducer;
    private final Map<Long, Long> postViewCounts = new ConcurrentHashMap<>();

    public void incrementViewCount(Long postId) {
        postViewCounts.merge(postId, 1L, Long::sum);
    }
    @Scheduled(fixedRateString = "${app.send-view-events:30000}")
    public void sendPostViewEvents() {
        if (postViewCounts.isEmpty()) {
            return;
        }
        Map<Long, Long> viewsToSend = new HashMap<>(postViewCounts);
        postViewCounts.clear();

        viewsToSend.forEach((postId, views) -> {
            PostViewEvent event = new PostViewEvent(postId, views);
            kafkaEventProducer.sendEvent(event);
            log.info("Sent view event for post ID {} with {} views", postId, views);
        });
    }
}
