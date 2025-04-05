package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostViewEventProducer extends KafkaProducerService {

    public PostViewEventProducer(KafkaTemplate<String, PostViewEvent> kafkaTemplate,
                                 ObjectMapper objectMapper,
                                 CustomKafkaProperties customKafkaProperties,
                                 UserMapper userMapper,
                                 @Value("${spring.kafka.topic.post-views-topic}") String topic) {
        super(kafkaTemplate, objectMapper, topic);
    }

    @Async("asyncTaskExecutor")
    public void produceViewPostEventAsync(long postId, Long visitorId) {
        produceViewPostEvent(postId, visitorId);
    }

    public void produceViewPostEvent(long postId, Long visitorId) {

        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .userId(visitorId)
                .build();
        super.sendMessage(postViewEvent);
        log.info("Sending PostViewEvent to message broker. Post : {}", postId);
    }
}
