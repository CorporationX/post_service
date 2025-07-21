package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDto(
        @NotNull
        Long id,
        @NotBlank
        String content
) {
}
