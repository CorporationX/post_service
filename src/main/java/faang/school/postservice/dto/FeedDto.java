package faang.school.postservice.dto;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDto {
    private Long userId;
    private String username;
    private Long postId;
    private String content;
    private int likes;
    private List<RedisCommentDto> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
