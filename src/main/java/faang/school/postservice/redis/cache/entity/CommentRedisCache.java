package faang.school.postservice.redis.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("comments")
public class CommentRedisCache implements Serializable {

    @Id
    private long id;

    @Reference
    @ToString.Exclude
    private AuthorRedisCache author;

    private String content;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentRedisCache that = (CommentRedisCache) o;
        return getId() == that.getId() && getLikesCount() == that.getLikesCount() && getCreatedAt().equals(that.getCreatedAt());
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(getId());
        result = 31 * result + Long.hashCode(getLikesCount());
        result = 31 * result + getCreatedAt().hashCode();
        return result;
    }
}
