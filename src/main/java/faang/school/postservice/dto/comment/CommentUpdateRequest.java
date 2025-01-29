package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for {@link faang.school.postservice.model.Comment}
 */
public record CommentUpdateRequest(@NotNull @Positive Long id,
                                   @Size(max = 4096) @NotBlank String content) {
}