package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        @NotBlank
        @Size(min = 1, max = 4096)
        String content,
        @NotNull
        Long authorId,
        long likeCount,
        @NotNull
        Long postId,
        LocalDateTime createAt,
        LocalDateTime updatedAt) {
}
