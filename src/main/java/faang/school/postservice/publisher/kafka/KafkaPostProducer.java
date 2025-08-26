package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.FollowServiceClient;
import faang.school.postservice.config.properties.AppKafkaProperties;
import faang.school.postservice.dto.event.PostCreatedKafkaEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AppKafkaProperties appKafkaProperties;
    private final FollowServiceClient followServiceClient;

    public void publishPostCreated(Post post) {
        List<Long> followerIds = safeList(followServiceClient.getFollowerIds(post.getAuthorId()));
        long publishedAt = post.getPublishedAt() == null
                ? System.currentTimeMillis()
                : post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        PostCreatedKafkaEvent event = PostCreatedKafkaEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .publishedAtEpochMillis(publishedAt)
                .followerIds(followerIds)
                .build();

        String topic = appKafkaProperties.getTopics().getPosts();
        kafkaTemplate.send(topic, String.valueOf(post.getId()), event)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send post-created event for post {} to topic {}",
                                post.getId(), topic, ex);
                    } else {
                        log.info("Post-created event sent for post {} to topic {}", post.getId(), topic);
                    }
                });
    }

    private static <T> List<T> safeList(List<T> list) {
        return list == null ? List.of() : list;
    }
}
