package faang.school.postservice.dto.Post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;
    private boolean deleted;
}
