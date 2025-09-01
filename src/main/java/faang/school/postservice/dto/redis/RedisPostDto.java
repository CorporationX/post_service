package faang.school.postservice.dto.redis;

import faang.school.postservice.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RedisPostDto {
    private long postId;
    private String content;
    private Long authorId;
    private List<Comment> comments;
    private Long viewCount;
    private Long likeCount;
    private LocalDateTime createdAt;
    private Long version;
}