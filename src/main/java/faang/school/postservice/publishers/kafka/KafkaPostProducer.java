package faang.school.postservice.publishers.kafka;

import faang.school.postservice.events.Event;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.kafka.PostEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {

    private final KafkaTemplate<String, Event> eventKafkaTemplate;
    private final PostEventService postEventService;

    @Value(value = "${spring.data.kafka.topic.posts_topic}")
    private String postsTopic;

    public void sendMessage(Post post) {
        PostEvent event = postEventService.getPostEventFromPost(post);
        CompletableFuture<SendResult<String, Event>> future = eventKafkaTemplate.send(postsTopic, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent post event = [{}] with offset = [{}]", event, result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send post event = [{}] due to : {}", event, ex.getMessage());
            }
        });
    }
}
