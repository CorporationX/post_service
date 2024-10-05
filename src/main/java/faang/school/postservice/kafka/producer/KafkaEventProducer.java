package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.FeedDto;
import faang.school.postservice.kafka.events.PostLikeEvent;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.events.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    @Value("${spring.kafka.topic-name.posts:posts}")
    private String postTopic;
    @Value("${spring.kafka.topic-name.post-views-topic}")
    private String postViewsTopic;
    @Value("${spring.kafka.topic-name.comments:comments}")
    private String commentTopic;
    @Value("${spring.kafka.topic-name.posts:likes}")
    private String likeTopic;
    @Value("${spring.kafka.topic-name.heat}")
    private String heatTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendPostFollowersEvent(PostFollowersEvent event) {
        kafkaTemplate.send(postTopic, event);
    }

    public void sendPostViewEvent(PostViewEvent event) {
        kafkaTemplate.send(postViewsTopic, event);
    }

    public void sendCommentEvent(CommentEvent event){
        kafkaTemplate.send(commentTopic, event);
    }

    public void sendLikeEvent(PostLikeEvent event){
        kafkaTemplate.send(likeTopic, event);
    }

    @Async
    public CompletableFuture<Void> sendFeedHeatEvent(FeedDto event) {
        try {
            kafkaTemplate.send(heatTopic, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send feed heat event", e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<Void> sendPostHeatEvent(PostDto event) {
        try {
            kafkaTemplate.send(heatTopic, event).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to send post heat event", e);
        }
        return CompletableFuture.completedFuture(null);
    }
}