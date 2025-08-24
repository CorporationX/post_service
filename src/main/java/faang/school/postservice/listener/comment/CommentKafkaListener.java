package faang.school.postservice.listener.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.facade.comment.CommentEventListenerFacade;
import faang.school.postservice.listener.AbstractKafkaListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentKafkaListener extends AbstractKafkaListener<CommentEvent> {
    private final CommentEventListenerFacade commentEventListenerFacade;
    public CommentKafkaListener(ObjectMapper objectMapper, CommentEventListenerFacade commentEventListenerFacade) {
        super(objectMapper, CommentEvent.class);
        this.commentEventListenerFacade = commentEventListenerFacade;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.comments.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listenCommentTopic(@Payload String message, Acknowledgment ack) {
        CommentEvent event = getEvent(message);
        log.info("Received a message from {}: {}", "paymentProps.getName()", event);

        commentEventListenerFacade.saveCommentInCache(event);
        ack.acknowledge();
    }
}
