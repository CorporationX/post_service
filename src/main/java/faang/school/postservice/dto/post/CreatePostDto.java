package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreatePostDto(
        @NotBlank
        @Schema(description = "Text content of the post")
        String content,
        @Schema(description = "ID of an existing user - author of the post")
        Long authorId,
        @Schema(description = "ID of an existing project - author of the post")
        Long projectId
) {
}
