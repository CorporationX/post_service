package faang.school.postservice.dto.comment;

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
public class ResourceDto {
    private Long id;

    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @NotBlank(message = "File key cannot be empty")
    private String fileKey;

    @NotBlank(message = "File type cannot be empty")
    private String fileType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
