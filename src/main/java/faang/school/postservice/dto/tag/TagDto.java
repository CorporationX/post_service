package faang.school.postservice.dto.tag;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record TagDto(
        @Nullable Long id,
        @NotBlank String name
) {
}
