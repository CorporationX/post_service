package faang.school.postservice.dto.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostOutputDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}