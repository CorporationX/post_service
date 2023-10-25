package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash(value = "FeedInCache")
@Builder
@ToString
public class FeedForRedis {
    // ключ id пользователя чей будет фид, набор значений (id постов) первые из 500 постов в его фиде
    @Id
    private long id;
    private LinkedHashSet<Long> postIds;

}
