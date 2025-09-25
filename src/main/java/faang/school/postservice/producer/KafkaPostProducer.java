package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FollowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FollowerRepository followerRepository;

    @Value("${app.kafka.topics.posts.name}")
    private String postsTopicName;

    public void sendPostCreatedEvent(Post post) {
        try {
            log.info("Preparing to send post created event for post ID: {}, author ID: {}",
                    post.getId(), post.getAuthorId());

            List<Long> followerIds = followerRepository.findFollowerIdsByUserId(post.getAuthorId());
            log.debug("Found {} followers for author ID: {}", followerIds.size(), post.getAuthorId());

            PostCreatedEvent event = PostCreatedEvent.builder()
                    .postId(post.getId())
                    .authorId(post.getAuthorId())
                    .content(sanitizeContent(post.getContent()))
                    .projectId(post.getProjectId())
                    .published(post.isPublished())
                    .followerIds(followerIds)
                    .createdAt(post.getCreatedAt())
                    .eventTimestamp(LocalDateTime.now())
                    .build();

            String partitionKey = String.valueOf(post.getAuthorId());

            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(postsTopicName, partitionKey, event);

            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    handleSendFailure(post, event, throwable);
                } else {
                    handleSendSuccess(post, result);
                }
            });

        } catch (Exception e) {
            log.error("Failed to prepare post created event for post ID: {}", post.getId(), e);
        }
    }

    private void handleSendSuccess(Post post, SendResult<String, Object> result) {
        log.info("Successfully sent post created event for post ID: {} to partition: {}, offset: {}",
                post.getId(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }

    private void handleSendFailure(Post post, PostCreatedEvent event, Throwable throwable) {
        log.error("Failed to send post created event for post ID: {} to Kafka topic: {}",
                post.getId(), postsTopicName, throwable);
    }

    private String sanitizeContent(String content) {
        if (content != null && content.length() > 1000) {
            return content.substring(0, 997) + "...";
        }

        return content;
    }

    public void sendPostCreatedEvent(Post post, List<Long> customFollowerIds) {
        try {
            log.info("Sending post created event with custom followers for post ID: {}", post.getId());

            PostCreatedEvent event = PostCreatedEvent.builder()
                    .postId(post.getId())
                    .authorId(post.getAuthorId())
                    .content(sanitizeContent(post.getContent()))
                    .projectId(post.getProjectId())
                    .published(post.isPublished())
                    .followerIds(customFollowerIds)
                    .createdAt(post.getCreatedAt())
                    .eventTimestamp(LocalDateTime.now())
                    .build();

            String partitionKey = String.valueOf(post.getAuthorId());
            kafkaTemplate.send(postsTopicName, partitionKey, event);

        } catch (Exception e) {
            log.error("Failed to send post created event with custom followers for post ID: {}",
                    post.getId(), e);
        }
    }
}