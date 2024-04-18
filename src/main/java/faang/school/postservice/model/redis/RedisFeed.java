package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.PostFeedDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.SortedSet;
import java.util.TreeSet;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Feed")
public class RedisFeed {
    @Id
    private Long userId;
    @TimeToLive
    private int ttl;
    @Version
    private long version;
    private SortedSet<PostFeedDto> postIds = new TreeSet<>();

    public void addPostFeedDto(PostFeedDto postFeedDto) {
        postIds.add(postFeedDto);
    }

    public void removePost (PostFeedDto postFeedDto) {
        postIds.remove(postFeedDto);
    }
}
