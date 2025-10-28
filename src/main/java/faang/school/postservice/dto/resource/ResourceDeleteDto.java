package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ResourceDeleteDto(
        List<@NotNull(message = "Resources cannot be empty")
        @Positive(message = "Resources cannot be negative") Long> resourceIds
) {
}