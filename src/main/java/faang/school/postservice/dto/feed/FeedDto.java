package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDto {
    private Long postId;
    private String authorName;
    private String content;
    private AtomicInteger likes;
    private ArrayDeque<RedisCommentDto> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
