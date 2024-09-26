package faang.school.postservice.publishers.kafka;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publishers.AbstractKafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventKafkaPublisher extends AbstractKafkaMessagePublisher<Like, LikeEvent> {
    private final LikeMapper likeMapper;

    public LikeEventKafkaPublisher(@Value("${kafka.topics.like_event}") String topic
            , KafkaTemplate<String, LikeEvent> likeEventKafkaTemplate,
                                   LikeMapper likeMapper) {
        super(topic, likeEventKafkaTemplate);
        this.likeMapper = likeMapper;
    }

    @Override
    public LikeEvent mapper(Like like) {
        return likeMapper.toEvent(like);
    }
}
