package faang.school.postservice.model.event;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.TreeSet;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "feed")
public class FeedEvent {
    @Id
    private Long id;
    private TreeSet<Long> postsId;
}
