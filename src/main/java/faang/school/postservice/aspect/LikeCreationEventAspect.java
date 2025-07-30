package faang.school.postservice.aspect;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.notification.LikeCreateNotificationEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeCreateEventPublisher;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Aspect
public class LikeCreationEventAspect {

    private final LikeCreateEventPublisher likeCreateEventPublisher;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void publishLikeCreationEvent(Post post) {
        UserDto userDto = userServiceClient.getUser(post.getAuthorId());
        Post postPublish = postService.getPostById(post.getId());
        LikeCreateNotificationEvent likeEvent = LikeCreateNotificationEvent.builder()
                .userId(userDto.getId())
                .postId(postPublish.getId())
                .build();

        likeCreateEventPublisher.publish(likeEvent);
    }
}
