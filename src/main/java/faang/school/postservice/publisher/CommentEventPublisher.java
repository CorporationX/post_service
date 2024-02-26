package faang.school.postservice.publisher;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.event_broker.CommentUserEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final AsyncCommentEventPublisher asyncPublisher;
    private final CommentEventMapper commentEventMapper;
    private final UserServiceClient userServiceClient;

    public void publish(CommentEvent event) {
        CommentUserEvent commentUserEvent = commentEventMapper.toUserEvent(event);
        UserDto userDto = userServiceClient.getUser(event.getAuthorId());
        commentUserEvent.setUserDto(userDto);
        asyncPublisher.asyncPublish(commentUserEvent);
    }
}
