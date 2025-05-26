package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private long likesCount;
}
