package faang.school.postservice.aspect;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.notification.CommentCreationNotificationEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.CommentCreationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Aspect
public class CommentCreationEventAspect {
    private final CommentCreationEventPublisher commentCreationEventPublisher;

    private final UserServiceClient userServiceClient;

    @Value("${notifications.symbol-amount-for-short-content}")
    private int symbolAmountForShortContent;

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.CommentCreationEventKafka)",
            returning = "result")
    public void publishCommentCreationEvent(Comment result) {
        log.info("publishCommentCreationEvent starts");
        UserDto postAuthor = userServiceClient.getUser(result.getPost().getAuthorId());
        UserDto commentAuthor = userServiceClient.getUser(result.getAuthorId());
        String shortContent = result.getContent().length() > symbolAmountForShortContent
                ? result.getContent().substring(0, symbolAmountForShortContent) + "..."
                : result.getContent();
        CommentCreationNotificationEvent event = CommentCreationNotificationEvent.builder()
                .shortContent(shortContent)
                .owner(postAuthor)
                .commentAuthorUserName(commentAuthor.getUsername())
                .build();
        log.info("publishCommentCreationEvent event: {}", event);

        commentCreationEventPublisher.publish(event);
    }
}
