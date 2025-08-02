package faang.school.postservice.publisher.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommonPublisher;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostEventPublisher implements MessagePublisher<PostEvent> {
    public static final String KAFKA_TOPIC = "post_published";
    private final UserServiceClient userServiceClient;
    private final CommonPublisher commonPublisher;

    @Override
    public void publish(PostEvent postEvent) {
        commonPublisher.sendKafka(KAFKA_TOPIC, postEvent);
    }

    public void createAndPublishMessage(Post post) {
        long userId = post.getAuthorId();
        List<Long> userFollowers = userServiceClient.getFollowers(userId)
                .stream()
                .map(UserDto::id)
                .toList();
        PostEvent event = new PostEvent(
                post.getId(),
                post.getContent(),
                userId,
                userFollowers,
                LocalDateTime.now());
        publish(event);
    }
}
