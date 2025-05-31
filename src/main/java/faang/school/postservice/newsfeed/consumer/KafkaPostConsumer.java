package faang.school.postservice.newsfeed.consumer;

import faang.school.postservice.dto.newsfeed.KafkaPostEvent;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.service.newsfeed.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
public class KafkaPostConsumer extends AbstractKafkaConsumer<KafkaPostEvent> {

    protected KafkaPostConsumer(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @Override
    protected void processEvent(KafkaPostEvent event) {
        List<Long> subscribersIds = event.subscribersIds();
        if (subscribersIds == null || subscribersIds.isEmpty()) {
            log.warn("KafkaPostEvent for postId {} has no subscribersIds. Cannot distribute to feeds.", event.postId());
            return;
        }

        KafkaTimePostIdEvent timePostIdEvent = new KafkaTimePostIdEvent(
                event.postId(),
                event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );

        subscribersIds.forEach(subscriberId -> {
            try {
                redisCacheService.addToFeed(subscriberId, timePostIdEvent);
            } catch (Exception e) {
                log.error("Failed to add post {} to feed for subscriber {} due to: {}",
                        event.postId(), subscriberId, e.getMessage(), e);
            }
        });

        try {
            redisCacheService.addToFeed(event.userId(), timePostIdEvent);
        } catch (Exception e) {
            log.error("Failed to add post {} to feed for author {} due to: {}",
                    event.postId(), event.userId(), e.getMessage(), e);
        }

        log.info("Distributed post {} to {} subscribers' feeds.", event.postId(), subscribersIds.size());
        redisCacheService.addToFeed(event.userId(), timePostIdEvent);
    }

    @Override
    public String getTopic() {
        return "posts";
    }
}
