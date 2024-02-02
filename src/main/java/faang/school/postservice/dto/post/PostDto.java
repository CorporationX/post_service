package faang.school.postservice.dto.post;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;

    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}