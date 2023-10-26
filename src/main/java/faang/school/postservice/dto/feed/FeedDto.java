package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.TimePostId;
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
public class FeedDto implements Comparable<FeedDto> {

    private long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private String smallFileId;
    private Integer likes;
    private List<RedisCommentDto> redisCommentDtos;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    @Override
    public int compareTo(FeedDto o) {
        int value;
        value = publishedAt.compareTo(o.publishedAt);

        if (value != 0) {
            return value;
        }
        value = Long.compare(postId, o.getPostId());

        return value;
    }
}
