package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    @NotNull(message = "Post id can't be null")
    private long id;

    @NotBlank(message = "Content can't be empty")
    private String content;

    private Long authorId;

    private Long projectId;

    private Boolean published;

    private LocalDateTime publishedAt;

    private Boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Future(message = "Scheduled date must be in the future")
    private LocalDateTime scheduledAt;
}