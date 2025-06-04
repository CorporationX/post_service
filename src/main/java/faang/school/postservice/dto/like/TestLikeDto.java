package faang.school.postservice.dto.like;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TestLikeDto(
        Long id,
        @NotBlank(message = "post content is empty")
        String content,
        @NotNull(message = "post author is empty")
        Long authorId,
        @NotNull(message = "project link is empty")
        Long projectId,
        Long likePostCount,
        Long likeCommentCount
) {
}
