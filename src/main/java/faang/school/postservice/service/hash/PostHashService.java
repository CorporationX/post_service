package faang.school.postservice.service.hash;

import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.repository.hash.PostHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostHashService {
    private final PostHashRepository postHashRepository;
    private final PostEventMapper postEventMapper;
    @Value("${feed.ttl}")
    private long ttl;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void savePost(PostEvent postEvent, Acknowledgment acknowledgment) {
        boolean exists = postHashRepository.findById(postEvent.getPostId()).isPresent();
        if (exists) {
            acknowledgment.acknowledge();
            return;
        }

        PostHash postHash = postEventMapper.toPostHash(postEvent);
        postHash.setTtl(ttl);
        postHashRepository.save(postHash);
        acknowledgment.acknowledge();
    }

}
