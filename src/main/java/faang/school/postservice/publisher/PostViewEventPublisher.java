package faang.school.postservice.publisher;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Setter
@Getter
@Component
@Aspect
@EnableAspectJAutoProxy
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent, Post> {

    @Value("${spring.data.redis.channels.post-view}")
    private String postViewChannel;

    public PostViewEventPublisher(RedisTemplate<String, PostViewEvent> eventRedisTemplate, UserContext userContext) {
        super(eventRedisTemplate, userContext);
    }

    @AfterReturning(
            pointcut = "@annotation(faang.school.postservice.annotations.SendPostViewEventToAnalytics)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue) {
        if (returnValue instanceof Post post) {
            sendEntityToAnalytics(post, postViewChannel);
        } else if (returnValue instanceof List<?> posts) {
            posts.stream()
                    .map(post -> (Post) post)
                    .forEach(post -> sendEntityToAnalytics(post, postViewChannel));
        }
    }

    @Override
    public PostViewEvent createEvent(Post post, Long actorId) {
        return new PostViewEvent(post.getId(), post.getAuthorId(), actorId, LocalDateTime.now());
    }
}
