package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class CommentConsumer {
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(
            topics = "comment-publication",
            groupId = "group"
    )
    public void listen(CommentDto commentDto) {
        log.info("PostConsumer has received: {}", commentDto);
        addComment(commentDto.getPostId(), commentDto);
    }

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void addComment(long postId, CommentDto comment) {
        RedisPost redisPost = redisPostRepository.findById(postId).orElseThrow();
        PostCacheDto post = redisPost.getPostCacheDto();
        post.setComments(new LinkedHashSet<>());
        LinkedHashSet<CommentDto> comments = post.getComments();

        if (comments.size() >= 3) {
            comments.remove(comments.iterator().next());

            PostCacheDto updatedPost = getPostCacheDto(comment, post, comments);
            redisPost.setPostCacheDto(updatedPost);

            try {
                redisPostRepository.save(redisPost);
            } catch (OptimisticLockingFailureException ex) {
                log.error("Error occurred while updating the comment for post with ID: " + postId);
                log.error(ex.getMessage());
            }
        } else {
            PostCacheDto updatedPost = getPostCacheDto(comment, post, comments);
            redisPost.setPostCacheDto(updatedPost);
            redisPostRepository.save(redisPost);
        }
    }

    private PostCacheDto getPostCacheDto(CommentDto comment, PostCacheDto post, LinkedHashSet<CommentDto> comments) {
        PostCacheDto postCacheDto = new PostCacheDto();
        postCacheDto.setPostId(post.getPostId());
        postCacheDto.setComments(new LinkedHashSet<>(comments));
        postCacheDto.getComments().add(comment);
        return postCacheDto;
    }
}
