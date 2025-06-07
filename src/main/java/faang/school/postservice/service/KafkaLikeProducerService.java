package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
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
    private final KafkaTemplate<String, PostDto> kafkaTemplatePostDto;
    private final String LOG_LIKE_SENT_TO_KAFKA_TOPIC = "Like {} is sent to KafkaTopic {}";
    private final String LOG_POST_SENT_TO_KAFKA_TOPIC = "Post {} is sent to KafkaTopic {}";

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

    public void send(String topicName, PostDto postDto) {
        var future = kafkaTemplatePostDto.send(topicName, postDto);
        future.whenComplete((sendResult, exception) -> {
            if(exception != null){
                future.completeExceptionally(exception);
            } else {
                future.complete(sendResult);
            }
            log.info(LOG_POST_SENT_TO_KAFKA_TOPIC, postDto, topicName);
        });
    }
}
