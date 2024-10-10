package faang.school.postservice.model.dto.comment;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record CommentRequestDto(
        Long id,
        @NotBlank(message = "Content cannot be null or empty")
        @Size(max = 4096, message = "Content can not exceed 4096 characters")
        String content,
        @NotNull(message = "Post ID cannot be null")
        @Positive
        Long postId
) {
}