package faang.school.postservice.messaging;

import faang.school.postservice.dto.post.KafkaPostView;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaPostViewProducer {
    @Value("${spring.data.kafka.topics.post-views.name}")
    private String topicName;
    private KafkaTemplate<String, KafkaPostView> kafkaTemplate;

    public void sendMessage(KafkaPostView kafkaPostView) {
        CompletableFuture<SendResult<String, KafkaPostView>> future = kafkaTemplate.send(topicName, kafkaPostView);
    }
}
