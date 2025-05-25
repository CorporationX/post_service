package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.kafkaevents.CommentEvent;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentEventListener {
    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.comment}",
            containerFactory = "commentEventListenerContainerFactory"
    )
    public void handleEvent(CommentEvent event) {}
}
