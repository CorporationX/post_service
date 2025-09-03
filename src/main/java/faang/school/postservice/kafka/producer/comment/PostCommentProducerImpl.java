package faang.school.postservice.kafka.producer.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.post.CommentEvent;

@RequiredArgsConstructor
@Component
public class PostCommentProducerImpl implements PostCommentProducer {
    @Value("${spring.kafka.topics.post-comment-publish.name}")
    private String publishCommentTopic;

    private final KafkaTemplate<String, CommentEvent> template;

    @Override
    public void onCommentPublished(CommentEvent event) {
        template.send(publishCommentTopic, String.valueOf(event.getPostId()), event);
    }
}
