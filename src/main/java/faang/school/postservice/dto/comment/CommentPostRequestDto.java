package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CommentPostRequestDto(
        @NotBlank(message = "Content cannot be blank")
        @Length(max = 4096, message = "Content cannot exceed 4096 characters")
        String content,

        @NotNull(message = "AuthorId cannot be null")
        @Positive(message = "AuthorId must be positive")
        Long authorId
) {
}