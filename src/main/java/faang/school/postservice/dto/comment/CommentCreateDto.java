package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;


@Builder
public record CommentCreateDto (
        @NotBlank(message = "Content must not be blank")
        @Size(max = 4096, message = "Content must be at most 4096 characters")
        String content,
        @NotNull(message = "Post ID must be provided")
        Long postId,
        String largeImageFileKey,
        String smallImageFileKey
) {}