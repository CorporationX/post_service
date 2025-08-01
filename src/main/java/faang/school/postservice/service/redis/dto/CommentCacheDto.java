package faang.school.postservice.service.redis.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class CommentCacheDto implements Serializable, Comparable<CommentCacheDto> {

    private Long id;

    private Long authorId;

    private Long likesCount;

    private String content;

    private LocalDateTime timestamp;

    @Override
    public int compareTo(CommentCacheDto o) {
        int cmp = this.timestamp.compareTo(o.timestamp);
        if (cmp != 0) return cmp;
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentCacheDto dto = (CommentCacheDto) o;
        return Objects.equals(id, dto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
