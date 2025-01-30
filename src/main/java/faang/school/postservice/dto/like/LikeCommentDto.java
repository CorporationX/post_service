package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikeCommentDto(
        @NotNull(groups = {After.class})
        Long id,
        @NotNull(groups = {Before.class, After.class})
        Long userId,
        @NotNull(groups = {Before.class, After.class})
        Long postId,
        @NotNull(groups = {Before.class, After.class})
        Long commentId
) {
    public interface After {}
    public interface Before {}
}