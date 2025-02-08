package faang.school.postservice.event_sender;

import faang.school.postservice.mapper.like.LikeEventMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.producer.KafkaLikeProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeEventSender {
    private final KafkaLikeProducer likeProducer;
    private final LikeEventMapper likeEventMapper;

    public void sendEvent(Like like) {
        LikeEvent likeEvent = likeEventMapper.toEvent(like);
        likeProducer.send(likeEvent);

        log.debug("Event like with id {} successfully sent to Kafka", like.getId());
    }
}
