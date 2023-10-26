package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostService postService;

    @KafkaListener(topics = "${spring.data.kafka.topics.post-view}", groupId = "post-group")
    public void listenerPostViewEvent(KafkaPostViewEvent kafkaPostViewEvent, Acknowledgment acknowledgment) {
        postService.saveView(kafkaPostViewEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
