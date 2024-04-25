package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.PostIdDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.TreeSet;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Feed")
public class RedisFeed implements Serializable {
    @Id
    private Long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;
    private TreeSet<PostIdDto> postIds = new TreeSet<>();

    public void addPostIdDto(PostIdDto postIdDto) {
        postIds.add(postIdDto);
    }

    public void removePost (PostIdDto postIdDto) {
        postIds.remove(postIdDto);
    }


    public void removeLastPostIdDto () {
        PostIdDto last = postIds.last();
        postIds.remove(last);
    }
}
