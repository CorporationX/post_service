package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PostEvent(
        @NotNull Long id,
        @NotBlank String content,
        @NotNull Long authorId,
        LocalDateTime time) {}
