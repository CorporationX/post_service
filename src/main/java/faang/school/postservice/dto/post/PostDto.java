package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    @NotBlank(message = "Content is not empty")
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private long numberLikes;
    private long numberViews;
}
