package faang.school.postservice.dto.post;

import faang.school.postservice.validation.OneOwnerOnly;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@OneOwnerOnly
public record PostRequestDto(
        @NotBlank(message = "Content should not be blank")
        @Size(max = 4096, message = "Content must be less than 4096 characters")
        String content,
        Long authorId,
        Long projectId
) {
}