package faang.school.postservice.model.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.ad.Ad;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Post")
public class RedisPost {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Set<Long> likedUserIds;
    private List<Comment> comments;
    private Ad ad;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
