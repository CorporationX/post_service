package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${kafka.topic.post-event}")
    private String postEventsTopic;

    public void publish(long postId,
                        long authorId,
                        String content) {
        List<Long> subscribers = subscriptionRepository.findFollowerIdByFolloweeId(authorId);
        PostEventDto postEventDto = PostEventDto
                .builder()
                .postId(postId)
                .authorId(authorId)
                .content(content)
                .subscriberIds(subscribers)
                .build();

        log.info("Publishing post event: post Id = {}, author Id = {}, content = {}",
                postId,
                authorId,
                content);
        try {
            kafkaTemplate.send(postEventsTopic, postEventDto);
            log.info("Successfully published post event: {}", postEventDto);
        } catch (Exception exception) {
            log.error("Failed to publish post event: {}", postEventDto, exception);
        }
    }
}
