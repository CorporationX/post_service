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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private long id;

    @NotBlank(message = "The post must contain text")
    private String content;

    @Min(value = 0, message = "The author's ID must be positive")
    @NotNull(message = "Author ID cannot be empty")
    private Long authorId;

    @Min(value = 0, message = "Project ID must be positive.")
    @NotNull(message = "Project ID cannot be empty")
    private Long projectId;

    boolean published;

    LocalDateTime publishedAt;

    LocalDateTime scheduledAt;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
