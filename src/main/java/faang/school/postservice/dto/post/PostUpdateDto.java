package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateDto(
        @NotBlank
        String content
) {
}
