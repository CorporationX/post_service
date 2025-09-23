package faang.school.postservice.model.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

@RedisHash("Feed")
public class Feed implements Serializable {

    @Id
    private Long subscriberId;
    private TreeSet<Long> postIds;
}
