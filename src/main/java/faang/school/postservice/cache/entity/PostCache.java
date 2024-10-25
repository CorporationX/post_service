package faang.school.postservice.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCache {
    @Id
    private Long id;

    private Long authorId;
    private String content;
    private long likeCount;
    private long viewCount;

    @TimeToLive
    private Long ttl;

    public void incrementLikes() {
        likeCount++;
    }

    public void incrementViews() {
        viewCount++;
    }
}
