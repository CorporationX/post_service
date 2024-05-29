package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

    @NotNull(message = "")
    @Min(value = 1, message = "Post ID cannot be less or equal 0")
    private long id;

    @NotBlank(message = "Meet name can't be empty")
    private String content;

    @NotNull(message = "Author ID cannot be null")
    @Min(value = 1, message = "Author ID cannot be less or equal 0")
    private Long authorId;

    @NotNull(message = "")
    @Min(value = 1, message = "Project ID cannot be less or equal 0")
    private Long projectId;

    private Boolean published;

    private LocalDateTime scheduledAt;

    private Boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
