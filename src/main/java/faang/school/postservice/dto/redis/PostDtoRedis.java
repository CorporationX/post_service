package faang.school.postservice.dto.redis;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDtoRedis implements Serializable {
    private long id;
    private String content;
    private Long authorId;
    private AtomicLong likes;
    private Queue<CommentDto> comments;
    private LocalDateTime createdAt;
}
