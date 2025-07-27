package faang.school.postservice.service.redis.dto;

import jakarta.persistence.Version;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentCacheDto implements Serializable, Comparable<CommentCacheDto> {

    private Long id;

    private Long authorId;

    private Long likesCount;

    private String content;

    private LocalDateTime timestamp;

    @Override
    public int compareTo(CommentCacheDto o) {
        return this.timestamp.compareTo(o.timestamp);
    }
}
