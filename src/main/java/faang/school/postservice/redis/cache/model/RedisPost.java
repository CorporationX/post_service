package faang.school.postservice.redis.cache.model;

import faang.school.postservice.dto.comment.CommentOutputDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@RedisHash("Post")
public class RedisPost implements Serializable, Comparable<RedisPost> {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<CommentOutputDto> comments;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    @TimeToLive
    private int timeToLive;
    private List<Long> redisUsersSubscribers;

    @Override
    public int compareTo(RedisPost other) {
        if (this.updatedAt != null && other.updatedAt != null) {
            int result = other.updatedAt.compareTo(this.updatedAt); // reversed
            if (result != 0) {
                return result;
            }
        }

        if (this.createdAt != null && other.createdAt != null) {
            return other.createdAt.compareTo(this.createdAt); // reversed
        }
        return 0;
    }
}
