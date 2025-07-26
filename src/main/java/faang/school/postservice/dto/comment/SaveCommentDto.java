package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO for creating or updating a comment")
public record SaveCommentDto(
        @Schema(description = "Comment text; must not be blank and must not exceed 4096 characters")
        @NotBlank(message = "Enter the comment text")
        @Size(max = 4096, message = "Comment text must not exceed 4096 characters")
        String content
) {
}
