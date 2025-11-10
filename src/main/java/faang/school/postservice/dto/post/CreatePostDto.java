package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

public record CreatePostDto(
    @NotBlank(message = "Field cannot be blank")
    String content,
    Long authorId,
    Long projectId
) {}
