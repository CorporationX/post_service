package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * DTO for {@link faang.school.postservice.model.Comment}
 */
@Builder
public record CreateCommentRequest(@NotNull @Positive Long userId,
                                   @NotBlank @Size(max = 4096) String content,
                                   @NotNull @Positive Long postId) {
}
