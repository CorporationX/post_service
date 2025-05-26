package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CommentDto(
        @NotNull
        Long id,
        @NotBlank
        String content,
        @NotNull
        Long authorId,
        List<Long> likeIds,
        @NotNull
        Long postId
) {
}
