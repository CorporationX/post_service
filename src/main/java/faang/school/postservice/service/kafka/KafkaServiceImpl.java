package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.event.PostCommentEvent;
import faang.school.postservice.event.PostLikeEvent;
import faang.school.postservice.event.PostPublishedEvent;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostCommentProducer;
import faang.school.postservice.producer.KafkaPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    private final UserServiceClient userServiceClient;
    private final KafkaPublisher<PostPublishedEvent> kafkaPostProducer;
    private final KafkaPublisher<PostViewEvent> kafkaPostViewProducer;
    private final KafkaPublisher<PostLikeEvent> kafkaLikeProducer;
    private final KafkaPostCommentProducer kafkaPostCommentProducer;
    private final UserContext userContext;

    @Async("executorKafkaSend")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPostEvent(Post post, long contextUserId) {
        userContext.setUserId(contextUserId);
        List<Long> followerIds = userServiceClient.getFollowersByAuthorNF(post.getAuthorId());
        if (followerIds.isEmpty()) {
            log.info("No followers found for post {}", post);
            return;
        }
        PostPublishedEvent event = PostPublishedEvent.builder()
                .postId(post.getId())
                .followersId(followerIds)
                .build();
        kafkaPostProducer.publish(event);
    }

    @Async("executorKafkaSend")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void sendPostViewEvent(Long postId) {
        kafkaPostViewProducer.publish(new PostViewEvent(postId));
    }

    @Async("executorKafkaSend")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void sendLikeEvent(Long postId, Long userId) {
        kafkaLikeProducer.publish(new PostLikeEvent(postId, userId));
    }

    @Async("executorKafkaSend")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void sendCommentEvent(PostCommentEvent postCommentEvent) {
        kafkaPostCommentProducer.publish(postCommentEvent);
    }

}
