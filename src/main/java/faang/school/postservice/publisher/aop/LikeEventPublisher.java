package faang.school.postservice.publisher.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.publisher.AbstractEventPublisher;
import faang.school.postservice.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikePostResponseDto> implements MessagePublisher<LikePostResponseDto> {

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              @Qualifier("likePostChannelTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }

    @Override
    public void publish(LikePostResponseDto likePostResponseDto) {
        super.publish(likePostResponseDto);
    }
}
