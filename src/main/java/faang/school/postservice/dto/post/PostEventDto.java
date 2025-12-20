package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record PostEventDto(
        @NotNull(message = "Post ID cannot be null")
        Long postId,
        Long authorId,
        Long projectId,
        @NotNull(message = "Follower IDs list cannot be null")
        List<Long> followerIds,
        @NotNull(message = "Published timestamp cannot be null")
        LocalDateTime publishedAt
) {
    @AssertTrue(message = "Must be sent only one field: authorId or projectId")
    @Schema(hidden = true)
    public boolean isExactlyOneAuthor() {
        return (authorId != null) ^ (projectId != null);
    }
}
