package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SendCommentDto(
        @NotNull(message = "It is necessary to indicate the author's id")
        Long authorId,
        @NotNull(message = "You must specify the post ID")
        Long postId,
        @Size(max = 4096, message = "Message don't be more 4096 chars!")
        @NotNull @NotBlank(message = "Be sure to include a comment")
        String content
) {
}