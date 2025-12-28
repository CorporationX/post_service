package faang.school.postservice.aop.comment;

import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisherAspect {

    private final ApplicationEventPublisher eventPublisher;

    @AfterReturning(
            pointcut = "@annotation(PublishCommentEvent)",
            returning = "comment"
    )
    public void publish(Comment comment) {

        if (comment == null) {
            return;
        }

        log.debug(
                "AOP: Comment created, publishing domain event [commentId={}]",
                comment.getId()
        );

        eventPublisher.publishEvent(
                new CommentCreatedEvent(comment)
        );
    }
}
