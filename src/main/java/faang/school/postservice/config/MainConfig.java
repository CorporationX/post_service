package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class MainConfig {
    @Value("${spring.data.redis.ttlMins}")
    private int ttlMins;


    @Value("${spring.data.redis.collections.feed.name}")
    private String feedKey;

    @Value("${spring.data.redis.collections.feed.ttlMins}")
    private int feedTtlMins;
    

    @Value("${spring.data.redis.collections.posts.name}")
    private String postsKey;

    @Value("${spring.data.redis.collections.posts.ttlMins}")
    private int postsTtlMins;


    @Value("${spring.data.redis.collections.comments.name}")
    private String commentsKey;

    @Value("${spring.data.redis.collections.comments.ttlMins}")
    private int commentsTtlMins;


    @Value("${spring.data.redis.collections.likes.name}")
    private String likesKey;

    @Value("${spring.data.redis.collections.likes.ttlMins}")
    private int likesTtlMins;
}
