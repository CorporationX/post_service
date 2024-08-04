package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class PostDto {
    private Long id;
    private String content;
    private Long projectId;
    private Long authorId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}