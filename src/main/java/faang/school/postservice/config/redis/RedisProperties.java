package faang.school.postservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RedisProperties {
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBanChannelName;

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentEventChannelName;

    @Value("${spring.data.redis.url.days_to_live}")
    private Long urlTtl;

    @Value("${spring.data.redis.feed_cache.max_size}")
    private Integer maxFeedSize;

    @Value("${spring.data.redis.feed_cache.key_prefix}")
    private String feedCacheKeyPrefix;

    @Value("${spring.data.redis.feed_cache.batch_size}")
    private Integer feedCacheBatchSize;

    @Value("${spring.data.redis.post_cache.key_prefix}")
    private String postCacheKeyPrefix;

    @Value("${spring.data.redis.post_cache.views_field}")
    private String postCacheViews;

    @Value("${spring.data.redis.post_cache.comments}")
    private String postCacheComments;

    @Value("${spring.data.redis.post_cache.max_comments_quantity}")
    private Integer postCacheMaxCommentsQuantity;

    @Value("${spring.data.redis.heat.max_posts_in_heat_feed}")
    private Integer maxPostsInHeatFeed;
}
