package faang.school.postservice.aspect;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.notification.CommentLikedEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.CommentLikedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CommentLikedEventAspect {

    private final UserServiceClient userServiceClient;
    private final CommentLikedEventPublisher publisher;

    @AfterReturning(
            value = "@annotation(faang.school.postservice.annotation.PublishCommentLikedEventKafka)",
            returning = "result"
    )
    public void publishCommentLikedEvent(Like result) {
        Like like = result;
        String content = like.getComment().getContent();

        UserDto authorDto = userServiceClient.getUser(like.getComment().getAuthorId());
        UserDto likerDto = userServiceClient.getUser(like.getUserId());

        publisher.publish(CommentLikedEvent.builder()
                .commentId(like.getComment().getId())
                .likerUsername(likerDto.getUsername())
                .owner(authorDto)
                .content(content)
                .build()
        );
    }
}