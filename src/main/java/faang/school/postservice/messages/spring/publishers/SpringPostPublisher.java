package faang.school.postservice.messages.spring.publishers;

import faang.school.postservice.dto.post.PostPublishedEventDto;
import faang.school.postservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@RequiredArgsConstructor
@Component
public class SpringPostPublisher {
    private final CacheService cacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePostPublished(PostPublishedEventDto event) {
        log.info("Save post to redis cache after publication, post id: {}, author id: {}", event.postId(), event.authorId());
        cacheService.savePost(event.postId(), event.authorId(), event.projectId());
    }
}
