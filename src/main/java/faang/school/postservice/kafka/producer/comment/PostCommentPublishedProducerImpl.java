package faang.school.postservice.kafka.producer.comment;

import faang.school.postservice.kafka.dto.comment.PostCommentPublishedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostCommentPublishedProducerImpl implements PostCommentPublishedProducer {
    @Value("${spring.kafka.topics.post-comment-published.name}")
    private String topic;

    private final KafkaTemplate<String, Object> template;

    @Override
    public void onCommentPublished(PostCommentPublishedDto dto) {
        template.send(topic, dto);
    }
}
