package faang.school.postservice.aop.commentanalysis;

import faang.school.postservice.dto.commentanalysis.AnalysisCommentsEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.commentanalysis.AnalysisCommentsProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class PublishCommentEventAspect {

    private final AnalysisCommentsProducer analysisCommentsProducer;

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.aop.comment.PublishCommentEvent)",
            returning = "result"
    )
    public void publishCommentEvent(JoinPoint joinPoint, Object result) {
        if (!(result instanceof Comment comment)) {
            log.error("Method annotated with @PublishCommentEvent did not return a Comment");
            return;
        }

        Post post = comment.getPost();
        if (post == null) {
            log.error("Comment id={} has no associated Post, skipping event publishing", comment.getId());
            return;
        }

        AnalysisCommentsEventDto dto = AnalysisCommentsEventDto.builder()
                .receiverId(post.getAuthorId())
                .authorId(comment.getAuthorId())
                .postId(post.getId())
                .commentId(comment.getId())
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Publishing analysis event for comment id={}", comment.getId());
        analysisCommentsProducer.publish(dto);
    }
}