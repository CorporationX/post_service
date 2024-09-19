package faang.school.postservice.aspect;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.kafka.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Aspect
@Component
@RequiredArgsConstructor
public class CommentAspect {

    private final KafkaCommentProducer kafkaCommentProducer;

    @AfterReturning(pointcut = "execution(* faang.school.postservice.service.CommentService.createComment(..))", returning = "result")
    public void afterCreateComment(CommentDto result) {
        CommentDto commentEvent = CommentDto.builder()
                .id(result.getId())
                .postId(result.getPostId())
                .authorId(result.getAuthorId())
                .createdAt(result.getCreatedAt())
                .build();
        kafkaCommentProducer.sendMessage(commentEvent);
    }
}
