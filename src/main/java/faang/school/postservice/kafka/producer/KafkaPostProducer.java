package faang.school.postservice.kafka.producer;

import faang.school.postservice.model.event.PostBySubscribersEvent;
import faang.school.postservice.properties.KafkaSettingsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostProducer {
    private static final int CHUNK_SIZE_KAFKA = 1000;

    private final KafkaSettingsProperties kafkaSettingsProperties;
    private final KafkaTemplate<String, PostBySubscribersEvent> kafkaTemplate;

    public void sendMessage(Long postId, List<Long> subscriberIds) {
        String postTopic = kafkaSettingsProperties.getPostTopic();
        IntStream.range(0, (subscriberIds.size() + CHUNK_SIZE_KAFKA - 1) / CHUNK_SIZE_KAFKA)
                .mapToObj(i -> subscriberIds.subList(i * CHUNK_SIZE_KAFKA, Math.min((i + 1) * CHUNK_SIZE_KAFKA, subscriberIds.size())))
                .forEach(chunk -> {
                    PostBySubscribersEvent event = new PostBySubscribersEvent(postId, chunk);
                    kafkaTemplate.send(postTopic, event);
                    log.info("Message sent to topic: {}, message: {}", postTopic, event);
                });
    }
}
