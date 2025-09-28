package faang.school.postservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private Long projectId;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
