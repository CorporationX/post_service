package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Feed")
public class CachedFeedDto implements Serializable {

    @Id
    private Long userId;

    private TreeSet<Long> postsIds = new TreeSet<>();
}
