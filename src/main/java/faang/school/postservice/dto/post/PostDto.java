package faang.school.postservice.dto.post;

import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDto {
    private String content;

    private Long authorId;

    private Long projectId;

    private boolean published;

    private LocalDateTime publishedAt;

    private LocalDateTime scheduledAt;

    private boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
