package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentKafkaEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;

    @Value("${spring.data.redis.post-comments-max}")
    private int maxCommentsInRedisPost;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenCommentEvent(CommentKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Comment event received. Comment ID: {}, Author ID: {}, Post ID: {}",
                event.getId(), event.getAuthorId(), event.getPostId());
        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);

        if (foundPost != null) {
            CommentRedis comment = commentMapper.fromKafkaEventToRedis(event);
            if (foundPost.getComments() == null) {
                log.info("No comments in Redis post. Add last comment");
                TreeSet<CommentRedis> comments = new TreeSet<>(Comparator.comparing(CommentRedis::getUpdatedAt).reversed());
                comments.add(comment);
                foundPost.setComments(comments);
            } else {
                foundPost.getComments().add(comment);
                foundPost.getComments().forEach(c -> System.out.println(c.getId()));
                while (foundPost.getComments().size() > maxCommentsInRedisPost) {
                    foundPost.getComments().remove(foundPost.getComments().last());
                }
                foundPost.getComments().forEach(c -> System.out.println(c.getId()));
            }
            redisPostRepository.save(foundPost);
        }
        acknowledgment.acknowledge();
    }
}
