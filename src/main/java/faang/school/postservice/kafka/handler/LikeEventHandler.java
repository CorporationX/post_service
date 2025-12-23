package faang.school.postservice.kafka.handler;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.service.like.RedisPostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventHandler implements EventHandler<LikeEvent>{

    private final RedisPostLikeService redisPostLikeService;

    @Override
    public void handle(LikeEvent event) {
        log.info("Processing LikeEvent for post {} by user {}",
                event.getPostId(), event.getLikedByUserId());

        try {
            redisPostLikeService.addLikeToPost(event);
            log.debug("Successfully processed LikeEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to process LikeEvent: {}", event, e);
            throw e;
        }
    }

    @Override
    public Class<LikeEvent> getEventType() {
        return LikeEvent.class;
    }
}
