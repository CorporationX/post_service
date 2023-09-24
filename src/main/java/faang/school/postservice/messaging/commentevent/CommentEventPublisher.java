package faang.school.postservice.messaging.commentevent;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.messaging.EventPublisher;
import faang.school.postservice.util.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEventPublisher implements EventPublisher<CommentEventDto> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;
    private final JsonMapper jsonMapper;

    @Override
    public void publish(CommentEventDto commentEventDto) {
        jsonMapper.toObject(commentEventDto)
                .ifPresent(s -> redisTemplate.convertAndSend(commentTopic.getTopic(), s));
        log.info(commentEventDto + " was send");
    }
}
