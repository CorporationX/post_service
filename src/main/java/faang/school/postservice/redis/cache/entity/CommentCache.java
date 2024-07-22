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
public class CommentCache implements Serializable, Comparable<CommentCache> {

    @Id
    private long id;

    @Reference
    @ToString.Exclude
    private AuthorCache author;

    private String content;
    private long likesCount;
    private long postId;
    private LocalDateTime createdAt;

    @Override
    public int compareTo(CommentCache o) {
        return o.getCreatedAt().compareTo(this.getCreatedAt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentCache that = (CommentCache) o;
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
