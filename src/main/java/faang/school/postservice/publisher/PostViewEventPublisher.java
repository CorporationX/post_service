package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostViewEventPublisher {

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;
    private final PostViewEventMapper mapper;

    @SneakyThrows
    public void publish(Post post) {
        PostViewEvent event = mapper.toEvent(post);

        CompletableFuture<SendResult<String, PostViewEvent>> future = kafkaTemplate.send("post-view", event);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Event was sent: {}", result);
            } else {
                log.error("Failed to send event: {}", e.getMessage());
            }
        });
    }
}
