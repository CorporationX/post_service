package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeEventProducer extends AbstractEventProducer {
    private final LikeMapper likeMapper;

    public KafkaLikeEventProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic likeKafkaTopic,
            LikeMapper likeMapper
    ) {
        super(kafkaTemplate, likeKafkaTopic);

        this.likeMapper = likeMapper;
    }

    @Async
    @Retryable
    public void sendLikeEvent(Like like) {
        LikeEventDto likeEventDto = likeMapper.toEventDto(like);
        sendEvent(likeEventDto, String.valueOf(like.getUserId()));
    }
}