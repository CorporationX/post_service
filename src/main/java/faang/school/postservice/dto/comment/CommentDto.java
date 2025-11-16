package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record CommentDto(
        Long id,
        @NotBlank
        @Size(max = 4096)
        String content,
        @NotNull
        Long authorId,
        @NotNull
        Long postId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        String largeImageFileKey,
        String smallImageFileKey
) {}
