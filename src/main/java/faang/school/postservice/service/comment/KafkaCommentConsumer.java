package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.kafka.KafkaCommentEvent;
import faang.school.postservice.model.redis.PostInRedis;
import faang.school.postservice.repository.redis.PostInRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommentConsumer {

    private final PostInRedisRepository postInRedisRepository;

    @KafkaListener(topics = "comments",
            containerFactory = "commentKafkaListenerContainerFactory")
    public void receiveCommentEvent(KafkaCommentEvent event) {
        Optional<PostInRedis> post = postInRedisRepository.findById(event.getPostId());
        if (post.isPresent()) {
            LinkedList<KafkaCommentEvent> comments = post.get().getComments();
            comments.addFirst(event);
            log.info("Comment {} was added into Redis Post {}", event, post);
        }
    }
}
