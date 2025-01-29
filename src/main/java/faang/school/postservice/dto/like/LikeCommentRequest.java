package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikeCommentRequest(@NotNull @Positive Long commentId, @NotNull @Positive Long userId) {
}
