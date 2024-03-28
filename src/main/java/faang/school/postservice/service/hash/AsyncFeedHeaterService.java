package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.mapper.CommentEventMapper;
import faang.school.postservice.mapper.LikePostEventMapper;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.LikePostEventPublisher;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncFeedHeaterService {
    private final PostEventMapper postEventMapper;
    private final CommentEventMapper commentEventMapper;
    private final LikePostEventMapper likeEventMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostEventPublisher postEventPublisher;
    private final CommentEventPublisher commentEventPublisher;
    private final LikePostEventPublisher likeEventPublisher;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void publishBatchPost(List<Long> userBatch) {
        userBatch.forEach(userId -> {
            List<Post> publishedPostsByUser = postRepository.findLatestPublishedByUserId(userId);
            publishedPostsByUser.forEach(post -> {
                postEventPublisher.heatPublish(postEventMapper.toPostEvent(post));
                commentRepository.findLatestByPostId(post.getId()).forEach(comment -> {
                    CommentEvent commentEvent = commentEventMapper.toEvent(comment);
                    commentEventPublisher.heaPublish(commentEvent);
                });
                List<Like>likes = likeRepository.findByPostId(post.getId());
                likes.forEach(like -> {
                    LikePostEvent likeEvent = likeEventMapper.toEvent(like);
                    likeEventPublisher.heatPublish(likeEvent);
                });
            });
        });
    }
}
