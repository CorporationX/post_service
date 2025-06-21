package faang.school.postservice.cash;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.SortedSet;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@RedisHash("NewsFeed")
public class NewsFeed {
    @Id
    Long userId;
    SortedSet<Long> posts;
}
