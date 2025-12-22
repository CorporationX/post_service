package faang.school.postservice.dto.event;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record PostViewEvent(
        @NotNull
        Long postId,
        @NotNull
        Long viewerId,
        @NotNull
        Instant viewedAt
) {
}