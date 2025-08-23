package faang.school.postservice.kafka.producer.comment;

import faang.school.postservice.dto.comment.CommentDto;
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
    public void onCommentPublished(CommentDto dto) {
        template.send(topic, String.valueOf(dto.postId()), dto);
    }
}
