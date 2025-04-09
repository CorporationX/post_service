package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncModerationService {

    private final BatchProcessorService batchProcessorService;

    @Async("fileUploadTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void moderateBatchAsync(List<Post> batch) {
        try {
            batchProcessorService.processBatch(batch);
        } catch (Exception e) {
            log.error("Batch moderation failed. Size: {}", batch.size(), e);
        }
    }
}
