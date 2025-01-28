package faang.school.postservice.model;

import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@RedisHash(value = "${spring.data.redis.feed.key-prefix}", timeToLive = 86400)
public class Feed {
    private long userId;
    private HashSet<Long> posts = new LinkedHashSet<>();

}
