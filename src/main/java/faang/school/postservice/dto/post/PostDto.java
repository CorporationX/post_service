package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank
    private String content;

    private Long authorId;
    private Long projectId;

    private Boolean published;
    private LocalDateTime publishedAt;
    private List<Long> likesIds;
    private Integer likeCount;

    private LocalDateTime scheduledAt;
}
