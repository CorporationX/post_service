package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateDto(
        @NotBlank(message = "the content is irrelevant or an empty string ")
        String content
) {
}
