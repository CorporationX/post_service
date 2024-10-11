package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.model.CommentEvent;
import faang.school.postservice.redis.model.PostCache;
import faang.school.postservice.redis.repository.PostCacheRepository;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventConsumer {

    @Value("${spring.data.kafka.topics.comment_topic.size:4}")
    private int topicSize;

    private PostCacheRepository postCache;
    private PostCacheService postCacheService;

    @KafkaListener(topics = "likes", groupId = "group1")
    public void listener(CommentEvent commentEvent) {
        log.info("Received message [{}]", commentEvent);
        PostCache post = postCache.findById(commentEvent.postId()).orElseThrow();
        int commentSize = post.getComments().size();
        if (commentSize > topicSize) {
            post.getComments().remove(0);
        }
        post.getComments().add(commentEvent);
        postCache.save(post);
    }
}
