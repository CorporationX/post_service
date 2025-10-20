package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record PostCreateDraftDto(
        @NotBlank
        String content,
        Long projectId
) {

}
