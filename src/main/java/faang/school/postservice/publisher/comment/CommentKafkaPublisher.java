package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.kafka.KafkaCommentTopicConfigurationProperties;
import faang.school.postservice.event.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentKafkaPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaCommentTopicConfigurationProperties commentProps;

    public void sendMessage(CommentEventDto commentEventDto) {
        kafkaTemplate.send(commentProps.getName(), commentEventDto).thenAccept(result ->
                        log.info("Comment event {} sent to Kafka topic {}", commentEventDto, commentProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send comment event to Kafka topic '{}'. Error: {}",
                            commentProps.getName(), ex.getMessage());
                    return null;
                });
    }
}
