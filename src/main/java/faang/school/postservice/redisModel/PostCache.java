package faang.school.postservice.redisModel;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.VerifiedStatus;
import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@RedisHash(value = "posts", timeToLive = 86400)
public class PostCache {
    @Id
    private Long postId;

    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private LocalDateTime publishedAt;
    private VerifiedStatus verifiedStatus;
    private int likeCount;
    private List<CommentDto> latestComments;
}
