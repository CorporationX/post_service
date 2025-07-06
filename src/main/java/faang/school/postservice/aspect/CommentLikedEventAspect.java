package faang.school.postservice.aspect;

import faang.school.postservice.dto.notification.CommentLikedEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.CommentLikedEventPublisher;
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
public class CommentLikedEventAspect {

    private final CommentLikedEventPublisher publisher;

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.PublishCommentLikedEventKafka)",
            returning = "result"
    )
    public void publishCommentLikedEvent(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        long commentId = (long) args[0];
        Like like = (Like) result;

        publisher.publish(CommentLikedEvent.builder()
                .likeId(like.getId())
                .commentId(commentId)
                .build()
        );
    }
}
