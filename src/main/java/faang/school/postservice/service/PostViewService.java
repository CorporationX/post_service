package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.KafkaPostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.newsfeed.producer.OutboxPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostViewService {

    private final PostRepository postRepository;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final OutboxPostViewProducer outboxPostViewProducer;

    @Transactional
    public void recordPostView(Long postId) {
        Long userId = userContext.getUserId();
        userValidator.validateUserExist(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post " + postId));

        KafkaPostViewEvent event = KafkaPostViewEvent.builder()
                .postId(postId)
                .userId(userId)
                .authorId(post.getAuthorId())
                .build();

        outboxPostViewProducer.saveToOutbox(event);
        log.info("Post view event for post {} by user {} sent to outbox", postId, userId);
    }
}
