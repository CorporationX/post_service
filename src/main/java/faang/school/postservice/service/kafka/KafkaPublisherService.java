package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String messageLogInfo = "Object: {} send success in topic: {}";
    @Value("${spring.kafka.topic.producer.post-views}")
    private String postView;

    public void publishingPostToKafka(PostDto postDto) {
        kafkaTemplate.send(postView, postDto);
        log.info(messageLogInfo, postDto.getClass(), postView);
    }
}