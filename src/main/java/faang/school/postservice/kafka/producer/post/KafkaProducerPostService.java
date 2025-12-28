package faang.school.postservice.kafka.producer.post;

import faang.school.postservice.kafka.dto.PublishPostEventKafkaDto;
import faang.school.postservice.kafka.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class KafkaProducerPostService {

    private final KafkaProducerService kafkaProducerService;

    @Value("${spring.kafka.topics.publish-post}")
    private String publishPostEventTopic;

    public void sendPublishPostEvent(Long postId, List<Long> subscribersIds) {
        PublishPostEventKafkaDto eventToSend = new PublishPostEventKafkaDto(
                postId,
                subscribersIds);
        kafkaProducerService.sendMessage(publishPostEventTopic, eventToSend);
    }
}
