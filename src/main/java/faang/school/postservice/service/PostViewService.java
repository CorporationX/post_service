package faang.school.postservice.service;

import faang.school.postservice.config.kafka.KafkaPostViewProducer;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostViewService {
    private final KafkaPostViewProducer kafkaPostViewProducer;

    public void processPostView(Long postId, Long userId) {
        PostViewEvent event = new PostViewEvent(postId, userId, LocalDateTime.now());
        kafkaPostViewProducer.sendPostViewEvent(event);
    }
}
