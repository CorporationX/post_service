package faang.school.postservice.dto.tag;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagDto(
        @Nullable Long id,
        @NotNull @NotBlank String name
) {
}
