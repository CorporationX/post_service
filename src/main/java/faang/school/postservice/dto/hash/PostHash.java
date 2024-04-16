package faang.school.postservice.dto.hash;

import faang.school.postservice.dto.CommentDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingDeque;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostHash")
public class PostHash implements Serializable {

    @Id
    private Long id;

    @NotBlank(message = "Content is required")
    private String content;

    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
    private LinkedBlockingDeque<CommentDto> comments =
            new LinkedBlockingDeque<>(3);

    public void addComment(CommentDto newComment) {
        if (comments.size() >= 3) {
            comments.pollFirst();
        }
        comments.offerLast(newComment);
    }

    @Version
    private long version;

    @TimeToLive
    private long timeToLive;
}
