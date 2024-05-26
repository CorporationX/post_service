package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Long id;
    @NonNull
    @NotBlank(message = "Content is required")
    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}