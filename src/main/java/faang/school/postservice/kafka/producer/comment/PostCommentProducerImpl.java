package faang.school.postservice.kafka.producer.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.post.PostCommentEvent;

@RequiredArgsConstructor
@Component
public class PostCommentProducerImpl implements PostCommentProducer {
    @Value("${spring.kafka.topics.post-comment-publish.name}")
    private String publishCommentTopic;

    private final KafkaTemplate<String, PostCommentEvent> template;

    @Override
    public void onCommentPublished(PostCommentEvent dto) {
        template.send(publishCommentTopic, String.valueOf(dto.getPostId()), dto);
    }
}
