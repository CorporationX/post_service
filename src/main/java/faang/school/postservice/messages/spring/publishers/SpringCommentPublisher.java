package faang.school.postservice.messages.spring.publishers;

import faang.school.postservice.dto.comment.ModelEventDto;
import faang.school.postservice.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class SpringCommentPublisher {
    private final CacheService cacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(ModelEventDto event) {
        log.info("Save the event to radish hash, author id {}", event.authorId());
        cacheService.saveAuthor(event.authorId(), event.modelId());
    }
}