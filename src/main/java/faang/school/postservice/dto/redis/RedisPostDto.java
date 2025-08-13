package faang.school.postservice.dto.redis;

import faang.school.postservice.dto.feed.CommentFeedDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisPostDto {
    private long postId;
    private String content;
    private Long authorId;
    private Long likeCount;
    private Long commentCount;
    private Long viewCount;
    private LocalDateTime createdAt;
    private Long version;
    private List<CommentFeedDto> comments;

    public RedisPostDto(long postId, String content, Long authorId, Long likeCount, Long commentCount, Long viewCount, LocalDateTime createdAt, Long version) {
        this.postId = postId;
        this.content = content;
        this.authorId = authorId;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.version = version;
    }
}
