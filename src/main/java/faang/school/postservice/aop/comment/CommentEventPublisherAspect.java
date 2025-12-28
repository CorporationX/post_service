package faang.school.postservice.aop.comment;

import faang.school.postservice.dto.kafka.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.producer.KafkaProducerService;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisherAspect {

    private final ApplicationEventPublisher eventPublisher;
    private final KafkaProducerService kafkaProducerService;

    @Value("${spring.kafka.topics.comments-feed}")
    private String topic;

    @AfterReturning(
            pointcut = "@annotation(PublishCommentEvent)",
            returning = "comment"
    )
    public void publish(Comment comment, UserDto user, Post post) {

        if (comment == null) {
            return;
        }

        log.debug(
                "AOP: Comment created, publishing domain event [commentId={}]",
                comment.getId()
        );

        CommentEventDto event = CommentMapper.toDto(comment, user, post);
        kafkaProducerService.sendMessage(topic, event);
    }
}
