package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePostDto(
        @NotNull
        @Schema(description = "ID of an existing post to be updated.")
        Long id,
        @NotBlank
        @Schema(description = "Updated text content of the post")
        String content
) {
}
