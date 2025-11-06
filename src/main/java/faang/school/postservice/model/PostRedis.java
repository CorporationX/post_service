package faang.school.postservice.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "postRedist", timeToLive = 60000000)
public class PostRedis {

}
