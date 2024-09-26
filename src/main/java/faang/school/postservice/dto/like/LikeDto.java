package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;

public record LikeDto(
        @Min(value = 1, message = "ID must be a positive number")
        long id,
        @Min(value = 1, message = "ID must be a positive number")
        Long userId) {
}
