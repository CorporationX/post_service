package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostDto(
        @NotBlank
        String content
) {
}
