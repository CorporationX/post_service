package faang.school.postservice.dto.tag;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TagCreateDto(@NotNull String name, @NotNull @Min(1) Long userId) {
}
