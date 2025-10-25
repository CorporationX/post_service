package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Ad(
        Long id,

        @NotNull(message = "Post cannot be null")
        Post post,

        @NotNull(message = "Buyer ID cannot be null")
        Long buyerId,

        @NotNull(message = "Appearances left cannot be null")
        Long appearancesLeft,

        @NotNull(message = "Start date cannot be null")
        LocalDateTime startDate,

        @NotNull(message = "End date cannot be null")
        LocalDateTime endDate) {
}
