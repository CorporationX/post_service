package faang.school.postservice.dto.comment.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RequestUpdateComment(
        @NotBlank(message = "Content cannot be empty")
        @Size(max = 4096, message = "Content cannot exceed 4096 characters")
        String content) {
}