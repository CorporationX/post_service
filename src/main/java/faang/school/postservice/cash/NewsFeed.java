package faang.school.postservice.cash;

import jakarta.persistence.PostLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
//@RedisHash("NewsFeed")
public class NewsFeed {
    @Id
    Long userId;
    SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
}
