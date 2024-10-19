package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.annotations.SendPostViewEventToAnalytics;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Aspect
@Component
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent, Post> {
    @Value("${spring.data.redis.channels.post-view}")
    private String postViewChannel;

    public PostViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  UserContext userContext,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, userContext, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(sendPostViewEventToAnalytics)",
            returning = "returnValue")
    public void publishPostEvent(Object returnValue, SendPostViewEventToAnalytics sendPostViewEventToAnalytics) {
        Class<?> clazz = sendPostViewEventToAnalytics.value();
        if (clazz == Post.class) {
            Post post = (Post) returnValue;
            PostViewEvent postViewEvent = convert(post);
            publish(postViewEvent);
        }
        if (clazz == List.class) {
            List<?> posts = (List<?>) returnValue;
            posts.stream()
                    .map(post -> (Post) post)
                    .forEach(post -> {
                        PostViewEvent postViewEvent = convert(post);
                        publish(postViewEvent);
                    });
        }
    }

    @Override
    protected PostViewEvent convert(Post post) {
        long actorId = actorId();
        return new PostViewEvent(post.getId(), post.getAuthorId(), actorId, LocalDateTime.now());
    }

    @Override
    protected String getTopicName() {
        return postViewChannel;
    }
}
