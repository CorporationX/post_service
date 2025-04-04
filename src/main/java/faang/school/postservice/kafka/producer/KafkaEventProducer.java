package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.events.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class KafkaEventProducer {

    @Value("${spring.kafka.topic-name.heat-feed}")
    private String heatFeedTopic;

    @Value("${spring.kafka.topic-name.heat-posts}")
    private String heatPostsTopic;

    @Value("${spring.kafka.topic-name.posts}")
    private String postTopic;

    @Value("${spring.kafka.topic-name.likes}")
    private String likeTopic;

    @Value("${spring.kafka.topic-name.comments}")
    private String commentTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public CompletableFuture<Void> sendFeedHeatEvent(FeedDto event) {
        return kafkaTemplate.send(heatFeedTopic, event)
                .thenApply(sendResult -> null);
    }

    public CompletableFuture<Void> sendPostHeatEvent(PostDto event) {
        return kafkaTemplate.send(heatPostsTopic, event)
                .thenApply(sendResult -> null);
    }

    public void sendPostFollowersEvent(PostFollowersEvent event) {
        kafkaTemplate.send(postTopic, event);
    }

    public void sendLikeEvent(PostLikeEvent event) {
        kafkaTemplate.send(likeTopic, event);
    }

    public void sendCommentEvent(CommentEvent event) {
        kafkaTemplate.send(commentTopic, event);
    }
}