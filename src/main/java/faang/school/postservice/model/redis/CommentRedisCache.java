package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("comments")
public class CommentRedisCache implements Serializable, Comparable<CommentRedisCache> {

    @Id
    private long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;

    @Reference
    @ToString.Exclude
    private AuthorRedisCache author;

    private String content;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;

    @Override
    public int compareTo(@NonNull CommentRedisCache o) {

        if (this.likesCount == 0) {
            return -1;
        }

        if (o.likesCount == 0) {
            return 1;
        }

        return (int) (this.likesCount - o.likesCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentRedisCache that = (CommentRedisCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
