package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.dto.event.PostViewEventKafka;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.post_view.name}")
    public void listen(PostViewEventKafka postViewEvent, Acknowledgment acknowledgment) {
        postHashService.addPostView(postViewEvent);
        acknowledgment.acknowledge();
    }
}
