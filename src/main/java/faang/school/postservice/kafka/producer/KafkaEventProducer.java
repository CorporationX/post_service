package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.kafka.model.ViewEvent;
import faang.school.postservice.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer<T> {

    @Value("${spring.data.kafka.topics.like_topic}")
    private String likeTopic;
    @Value("${spring.data.kafka.topics.comment_topic}")
    private String commentTopic;
    @Value("${spring.data.kafka.topic-name.views}")
    private String viewTopic;

    private final LikeMapper likeMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendLikeEvent(LikeEventDto likeEventDto) {
        kafkaTemplate.send(likeTopic, likeMapper.toEvent(likeEventDto));
    }

    public void sendCommentEvent(CommentEvent commentEvent) {
        kafkaTemplate.send(commentTopic, commentEvent);
    }

    public void sendViewEvent(ViewEvent viewEvent) {
        kafkaTemplate.send(viewTopic, viewEvent);
    }
}
