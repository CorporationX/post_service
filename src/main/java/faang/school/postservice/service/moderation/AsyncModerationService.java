package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncModerationService {

    private final BatchProcessorService batchProcessorService;

    @Async("asyncModerationExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> moderateBatchAsync(List<Post> batch) {
        try {
            batchProcessorService.processBatch(batch);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }
}
