package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDto {
    private Long id;

    @NotBlank
    private String content;

    private Long authorId;
    private Long projectId;

    private Boolean published;
    private LocalDateTime publishedAt;
}
