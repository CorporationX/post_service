package faang.school.postservice.consumer;

import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Component
@Slf4j
public class KafkaCommentConsumer extends AbstractSimpleConsumer<CommentEvent> {

    public KafkaCommentConsumer(RedisPostRepository redisPostRepository) {
        super(redisPostRepository);
    }

    @Value("${feed.max-comment-size}")
    private int maxCommentSize;

    @KafkaListener(topics = "${spring.data.kafka.topics.comment_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        listenEvent(commentEvent, acknowledgment);
    }

    private void setNewComment(PostEvent postEvent, CommentEvent comment) {
        if (postEvent.getComments() == null) {
            TreeSet<CommentEvent> comments = new TreeSet<>(Comparator.comparing(CommentEvent::getCreatedAt).reversed());
            comments.add(comment);
            postEvent.setComments(comments);
        } else {
            postEvent.getComments().add(comment);
            while (postEvent.getComments().size() > maxCommentSize) {
                postEvent.getComments().remove(postEvent.getComments().last());
            }
        }
        log.debug("Added a comment from the author {} to the post {}", comment.getAuthorId(), postEvent.getId());
    }

    @Override
    protected void processEvent(CommentEvent event, PostEvent post) {
        setNewComment(post, event);
    }
}
