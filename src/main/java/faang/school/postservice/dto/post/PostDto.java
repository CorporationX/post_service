package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @NotNull
    @NotBlank
    @Min(1)
    @Max(4096)
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Integer> likeIds;
    private boolean isPublished;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private boolean published;
}