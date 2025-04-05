package faang.school.postservice.config.feed;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "newsfeed")
public record NewsFeedProperties(
        String prefix,
        Integer maxPosts,
        Integer pageSize,
        Integer batchSize,
        Integer commentsNumber
) {
}
