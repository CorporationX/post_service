package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostCreatedEvent;
import faang.school.postservice.model.Post;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PostCreatedEventPublisher extends AbstractEventPublisher<PostCreatedEvent, Post> {
    @Value("${spring.data.redis.channels.post-crated}")
    private String postCreatedTopic;

    public PostCreatedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     UserContext userContext,
                                     ObjectMapper objectMapper) {
        super(redisTemplate, userContext, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.SendPostCreatedEvent)",
            returning = "returnedValue")
    public void publishPostEvent(Object returnedValue) {
        Post post = (Post) returnedValue;
        PostCreatedEvent postCreatedEvent = convert(post);
        publish(postCreatedEvent);
    }

    @Override
    protected PostCreatedEvent convert(Post post) {
        return PostCreatedEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .build();
    }

    @Override
    protected String getTopicName() {
        return postCreatedTopic;
    }
}
