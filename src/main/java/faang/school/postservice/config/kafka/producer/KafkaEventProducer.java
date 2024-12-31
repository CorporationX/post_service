package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.CommentEventRecord;
import faang.school.postservice.event.FeedDto;
import faang.school.postservice.event.PostFollowersEventRecord;
import faang.school.postservice.event.PostViewEventRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public void sendPostFollowersEvent(PostFollowersEventRecord event) {
        String postTopic = kafkaProperties.getPostsTopic();
        String key = String.valueOf(event.postId());
        kafkaTemplate.send(postTopic, key, event);
    }

    public void sendPostViewEvent(PostViewEventRecord event) {
        String postViewsTopic = kafkaProperties.getPostsTopic();
        String key = String.valueOf(event.postId());
        kafkaTemplate.send(postViewsTopic, key, event);
    }

    public void sendCommentEvent(CommentEventRecord event) {
        String commentsTopic = kafkaProperties.getCommentsTopic();
        String key = String.valueOf(event.authorId());
        kafkaTemplate.send(commentsTopic, key, event);
    }

    public void sendFeedHeatEvent(FeedDto event) {
        String heatFeedsTopic = kafkaProperties.getHeatFeedsTopic();
        String key = String.valueOf(event.followerId());
        kafkaTemplate.send(heatFeedsTopic, key, event)
                .thenRun(() -> {
                })
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send feed heat event", ex);
                });
    }

    public void sendPostHeatEvent(PostDto event) {
        String heatPostsTopic = kafkaProperties.getHeatPostsTopic();
        String key = String.valueOf(event.getId());
        kafkaTemplate.send(heatPostsTopic, key, event)
                .thenRun(() -> {
                })
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send feed heat event", ex);
                });
    }
}