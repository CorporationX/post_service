package faang.school.postservice.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.model.Post;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostProducer extends AbstractEventProducer {
    private final UserServiceClient userServiceClient;

    public PostProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic postKafkaTopic,
            UserServiceClient userServiceClient
    ) {
        super(kafkaTemplate, postKafkaTopic);

        this.userServiceClient = userServiceClient;
    }

    @Async
    @Retryable
    public void sendPostEvent(Post post) {
        List<Long> postAuthorFollowersIds = userServiceClient.getFollowersIds(post.getAuthorId());

        PostEventDto postEventDto = PostEventDto.builder()
                .authorId(post.getAuthorId())
                .authorFollowersIds(postAuthorFollowersIds)
                .postId(post.getId())
                .build();

        sendEvent(postEventDto, String.valueOf(post.getAuthorId()));
    }
}
