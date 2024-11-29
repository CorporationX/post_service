package faang.school.postservice.aop.aspects;

import faang.school.postservice.dto.like.RedisPostLikeEvent;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.like.RedisPostLikeEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Lazy
@Component
public class PostLikeEventPublishingAspect {
    private final RedisPostLikeEventPublisher redisPostLikeEventPublisher;
    private final LikeMapper likeMapper;

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.PublishPostLikeEvent)",
            returning = "returnedValue"
    )
    @Async("redisPublisherAsyncThreadPool")
    public void publishLikeEvent(Object returnedValue) {
        Like like = (Like) returnedValue;
        RedisPostLikeEvent redisPostLikeEvent = likeMapper.toRedisPostLikeEvent(like);
        redisPostLikeEventPublisher.publish(redisPostLikeEvent);
    }
}
