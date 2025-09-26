package faang.school.postservice.dto.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostPublishedEvent {
    private Long postId;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
    private List<Long> subscribers;
}
