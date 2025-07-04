package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentCreateDto(

        @NotNull(message = "Post ID must not be null")
        Long postId,

        @NotBlank(message = "Content must not be blank")
        @Size(max = 4096, message = "Content must be less than 4096 characters")
        String content
) {
}
