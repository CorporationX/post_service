package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostCreateDraftDto(
        @NotBlank(message = "the content is irrelevant or an empty string ")
        String content
) {
}
