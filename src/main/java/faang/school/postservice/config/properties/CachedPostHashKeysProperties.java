package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.redis.cached-post-hash-keys")
public record CachedPostHashKeysProperties(
        String postId,
        String content,
        String authorId,
        String projectId,
        String publishedAt,
        String updatedAt,
        String views,
        String likesCount

) {
}
