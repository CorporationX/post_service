package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateCommentDto(
        @NotNull(message = "Be sure to include the comment id")
        @Positive(message = "post must be greater than zero")
        Long commentId,
        @NotNull @NotBlank(message = "Please enter a comment")
        @Size(max = 255, message = "Line size increased!")
        String content
) {
}