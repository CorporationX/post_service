package faang.school.postservice.properties.redis.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@Builder
@ConfigurationProperties("cache.comment")
public class RedisCommentProperties {
}
