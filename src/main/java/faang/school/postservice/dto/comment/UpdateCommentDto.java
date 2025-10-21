package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCommentDto(
        @NotNull(message = "you need to specify the author's id")
        Long authorId,
        @NotNull(message = "Be sure to include the comment id")
        Long commentId,
        @NotNull @NotBlank(message = "Please enter a comment")
        String content
) {
}