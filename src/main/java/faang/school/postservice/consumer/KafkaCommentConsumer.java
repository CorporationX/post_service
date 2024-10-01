package faang.school.postservice.consumer;


import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.dto.publishable.CommentEvent;
import faang.school.postservice.exception.kafka.NonRetryableException;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaCommentConsumer {
    private final PostService postService;
    private final CommentService commentService;
    private final RedisPostRepository redisPostRepository;

//    @Value("${spring.data.redis.max-comments-size}")
//    private int maxSize;

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topic-name.comments}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "containerFactory")
    public void listenCommentEvent(CommentEvent commentEvent, Acknowledgment ack) {
        long postId = commentEvent.getPostId();
        long commentId = commentEvent.getCommentId();
        CachedPostDto cachedPostDto = postService.getPostFromCache(postId);
        CommentDto commentDto = commentService.findById(commentId);

        if(commentDto==null){
            log.info("comment with id = {} not exist", commentId);
            throw new NonRetryableException(String.format("comment with id = %d not exist", commentId));
        }
        if (cachedPostDto == null) {
            log.info("post with id = {} not exist", postId);
            throw new NonRetryableException(String.format("post with id = %d not exist", postId));
        }

        cachedPostDto.addComment(commentDto, 3);
        redisPostRepository.save(cachedPostDto.getId(), cachedPostDto);
        log.info("added new comment to post with id = {}", postId);
        ack.acknowledge();
    }
}
