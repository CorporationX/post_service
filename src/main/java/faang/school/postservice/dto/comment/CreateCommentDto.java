package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentDto(
        @NotBlank(message = "Content cannot be blank")
        @Size(min = 1, max = 4096, message = "Content must be between 1 and 4096 characters")
        String content
) {
}
