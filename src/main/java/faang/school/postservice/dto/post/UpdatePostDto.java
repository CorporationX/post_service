package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdatePostDto(
        @NotBlank
        @Schema(description = "Updated text content of the post")
        String content
) {
}
