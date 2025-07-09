package faang.school.postservice.aspect;

import faang.school.postservice.dto.notification.PostLikedEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.PostLikedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PostLikedEventAspect {

    private final PostLikedEventPublisher publisher;

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.PublishPostLikedEventKafka)",
            returning = "result"
    )
    public void publishPostLikedEvent(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        long postId = (long) args[0];
        Like like = (Like) result;

        publisher.publish(PostLikedEvent.builder()
                .likeId(like.getId())
                .likerId(like.getUserId())
                .authorId(like.getPost().getAuthorId())
                .postId(postId)
                .build()
        );
    }
}