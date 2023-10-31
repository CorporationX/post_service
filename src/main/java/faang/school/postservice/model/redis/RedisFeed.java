package faang.school.postservice.model.redis;

import faang.school.postservice.dto.PostPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@RedisHash(value = "Feed")
public class RedisFeed implements Serializable {

    @Id
    private long userId;
    private LinkedHashSet<PostPair> posts;
}
