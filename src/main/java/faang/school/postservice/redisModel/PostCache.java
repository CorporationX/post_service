package faang.school.postservice.redisModel;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.VerifiedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@RedisHash(value = "posts")
public class PostCache implements Serializable {
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

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;
}
