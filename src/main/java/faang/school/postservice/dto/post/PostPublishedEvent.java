package faang.school.postservice.dto.post;

import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostPublishedEvent {
    private Long postId;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private List<Long> subscribers;
}
