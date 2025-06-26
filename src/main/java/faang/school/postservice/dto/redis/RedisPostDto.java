package faang.school.postservice.dto.redis;

import faang.school.postservice.dto.comment.CommentResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class RedisPostDto {

    private long postId;
    private long authorId;
    private String content;
    private Instant createdAt;
    private long viewsCount;
    private long likesCount;
    private List<CommentResponseDto> comments;
}
