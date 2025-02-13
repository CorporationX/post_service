package faang.school.postservice.dto.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UserCacheDto(
        @Positive(message = "Id must be a positive integer")
        @NotNull(message = "Id is required")
        Long userId,

        @Size(min = 5, max = 30, message = "Username must be between 5 and 30 characters")
        String username,
        List<Long> followeesIds) {
}
