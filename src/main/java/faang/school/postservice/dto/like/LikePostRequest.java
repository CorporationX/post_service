package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikePostRequest(@NotNull @Positive Long postId, @NotNull @Positive Long userId) {
}
