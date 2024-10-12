package faang.school.postservice.redis.cache.entity;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.NavigableSet;

@Getter
@Setter
@ToString(exclude = {"comments"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostCache implements Serializable, Comparable<PostCache> {

    @Id
    private long id;
    private NavigableSet<CommentDto> comments;
    private long authorId;
    private String content;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private long likesCount;
    private long viewsCount;

    @Override
    public int compareTo(PostCache o) {
        return o.getPublishedAt().compareTo(this.getPublishedAt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCache that = (PostCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
