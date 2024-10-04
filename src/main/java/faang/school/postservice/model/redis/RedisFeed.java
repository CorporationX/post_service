package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@RedisHash(value = "feed", timeToLive = 60)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisFeed implements Serializable {
    @Value(value = "${news-feed.feed.posts_size}")
    private int maxAmount;
    @Id
    private int userId;
    private LinkedHashSet<Long> postsIds = new LinkedHashSet<>(maxAmount);
    @Version
    private int version;
}
