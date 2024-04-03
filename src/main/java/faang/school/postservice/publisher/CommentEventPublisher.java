package faang.school.postservice.publisher;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.event_broker.CommentUserEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher extends AsyncEventPublisher<CommentUserEvent>{
    private final CommentEventMapper commentEventMapper;
    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;
    @Value("${spring.kafka.topics.heat_feed.comment}")
    private String heatCommentTopic;

    @Override
    protected String getTopicName() {
        return commentTopic;
    }

    @Override
    protected String getHeatTopicName() {
        return heatCommentTopic;
    }

    public void publish(CommentEvent event) {
        CommentUserEvent commentUserEvent = commentEventMapper.toUserEvent(event);
        UserDto userDto = userServiceClient.getUser(event.getAuthorId());
        commentUserEvent.setUserDto(userDto);
        asyncPublish(commentUserEvent);
    }

    public void heaPublish(CommentEvent event) {
        CommentUserEvent commentUserEvent = commentEventMapper.toUserEvent(event);
        UserDto userDto = userServiceClient.getUser(event.getAuthorId());
        commentUserEvent.setUserDto(userDto);
        asyncHeatPublish(commentUserEvent);
    }
}
