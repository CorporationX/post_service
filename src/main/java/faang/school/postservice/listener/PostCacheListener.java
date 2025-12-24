package faang.school.postservice.listener;

import faang.school.postservice.service.posts.PostCacheService;
import faang.school.postservice.service.posts.PostCreatedInternalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacheListener {

    private final PostCacheService postCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PostCreatedInternalEvent event) {
        try {
            postCacheService.save(event.post());
        } catch (Exception e) {
            log.error("Failed to cache post {} in Redis", event.post().getId(), e);
        }
    }
}