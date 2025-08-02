package faang.school.postservice.aspect;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.notification.LikeCreateNotificationEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeCreateEventPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Aspect
public class LikeCreationEventAspect {

    private final LikeCreateEventPublisher likeCreateEventPublisher;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    @AfterReturning(value = "@annotation(faang.school.postservice.annotation.LikeCreationEventKafka)",
            returning = "post")
    public void publishLikeCreationEvent(Post post) {
        UserDto userDto = userServiceClient.getUser(post.getAuthorId());
        Post postPublish = postService.getPostById(post.getId());
        LikeCreateNotificationEvent likeEvent = LikeCreateNotificationEvent.builder()
                .userDto(userDto)
                .postId(postPublish.getId())
                .build();

        likeCreateEventPublisher.publish(likeEvent);
    }
}
