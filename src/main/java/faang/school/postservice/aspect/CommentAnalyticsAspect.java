package faang.school.postservice.aspect;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.notification.CommentAnalytics;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentEventPublisher;
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
public class CommentAnalyticsAspect {

    private final CommentEventPublisher publisher;

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.PublishCommentEventKafka)",
            returning = "result"
    )
    public void publishCommentAnalytics(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        Long postId = (Long) args[0];
        CommentDtoResponse comment = (CommentDtoResponse) result;

        publisher.publish(CommentAnalytics.builder()
                .postId(postId)
                .authorId(comment.getAuthorId())
                .commentId(comment.getCommentId())
                .createdAt(comment.getCreateData())
                .build()
        );
    }
}