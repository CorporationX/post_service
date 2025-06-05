package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeProducerService {
    private final KafkaTemplate<String, LikeDto> kafkaTemplate;
    private final String LOG_LIKE_SENT_TO_KAFKA_TOPIC = "Like {} is sent to KafkaTopic {}";

    public void send(String topicName, LikeDto likeDto) {
        var future = kafkaTemplate.send(topicName, likeDto);
        future.whenComplete((sendResult, exception) -> {
            if(exception != null){
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            log.info(LOG_LIKE_SENT_TO_KAFKA_TOPIC, likeDto, topicName);
        });
    }
}
