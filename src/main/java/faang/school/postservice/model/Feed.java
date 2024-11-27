package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@RedisHash("Feed")
public class Feed implements Serializable {
    @Id
    private long userId;
    private LinkedHashSet<Long> postIds;
}
