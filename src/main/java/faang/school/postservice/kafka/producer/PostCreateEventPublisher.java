package faang.school.postservice.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.dto.post.PostCreate;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCreateEventPublisher extends ProduceHandler<PostCache> {

    private final UserContext userContext;
    @Value("${spring.kafka.topics.post-create.name:post_create}")
    private String postCreateTopic;
    @Value("${partitions.post-create:1000}")
    private int postCreatePartitions;
    private final UserServiceClient userServiceClient;
    private final KafkaSender kafkaSender;

    @Async("cacheExecutor")
    @Retryable(retryFor = {FeignException.FeignClientException.class, FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000))
    public void publish(PostCache postCache) {
        userContext.setUserId(postCache.getAuthorId());
        ListUtils.partition(userServiceClient.getUserFollowers(postCache.getAuthorId()),
                postCreatePartitions).forEach(partUserIds -> {
            kafkaSender.sendMessage(postCreateTopic,
                    PostCreate.builder()
                            .postId(postCache.getId())
                            .userIds(partUserIds)
                            .build());
        });
    }
}
