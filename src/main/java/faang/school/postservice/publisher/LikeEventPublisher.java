package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.like-event}")
    private String likeEventsTopic;

    public void publish(long likeId,
                        long postId,
                        long authorId) {
        LikeEventDto likeEventDto = LikeEventDto
                .builder()
                .likeId(likeId)
                .postId(postId)
                .authorId(authorId)
                .build();

        log.info("Publishing like event: like Id = {}, post Id = {}, author Id = {}",
                likeId,
                postId,
                authorId);
        try {
            kafkaTemplate.send(likeEventsTopic, likeEventDto);
            log.info("Successfully published like event: {}", likeEventDto);
        } catch (Exception exception) {
            log.error("Failed to publish like event: {}", likeEventDto, exception);
        }
    }
}
