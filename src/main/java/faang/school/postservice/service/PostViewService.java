package faang.school.postservice.service;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostViewService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final KafkaPostViewProducer kafkaPostViewProducer;

    @Transactional
    @Retryable(retryFor = { OptimisticLockException.class })
    public void handlePostView(long postId, long userId) {
        postValidator.validatePostExistence(postId);

        postRepository.incrementViewCount(postId);

        val postViewEvent = PostViewEvent.builder()
            .postId(postId)
            .userId(userId)
            .viewedAt(LocalDateTime.now())
            .build();

        kafkaPostViewProducer.sendMessage(postViewEvent);
    }
}
