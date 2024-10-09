package faang.school.postservice.config.kafka.producer;

import faang.school.postservice.dto.comment.CommentDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractProducer<CommentDto> {
    private final NewTopic commentTheme;

    public KafkaCommentProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                NewTopic commentTheme) {
        super(kafkaTemplate);
        this.commentTheme = commentTheme;
    }


    @Override
    public String getTheme() {
        return commentTheme.name();
    }
}
