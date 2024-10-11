package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private LocalDateTime publishedAt;
    private boolean deleted;
    private LocalDateTime createdAt;
}
