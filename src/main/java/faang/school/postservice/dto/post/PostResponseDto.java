package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record PostResponseDto(Long id, @NotBlank String content, Long authorId,
                              Long projectId, boolean published, LocalDateTime publishedAt,
                              LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted) {
}
