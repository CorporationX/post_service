package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateCommentDto(
        @NotBlank @Size(max = 4096, message = "The allowed maximum length is 4096 characters.") String content,

        @NotNull Long authorId
) {
}
