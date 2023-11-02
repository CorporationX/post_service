package faang.school.postservice.dto;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDto {

    private long userId;
    private String username;
    private String pictureFileId;
    private Long postId;
    private String content;
    private int likes;
    private List<RedisCommentDto> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
