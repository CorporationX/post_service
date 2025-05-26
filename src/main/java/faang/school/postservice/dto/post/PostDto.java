package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record PostDto(
        @NotNull
        Long id,
        @NotBlank
        String content,
        @NotNull
        Long authorId,
        List<Long> likeIds,
        List<Long> commentIds
) {
}
