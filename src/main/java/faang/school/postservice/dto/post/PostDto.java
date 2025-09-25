package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        Long id,
        Long authorId,
        Long projectId,
        @NotBlank
        String content,
        long likes,
        long views,
        LocalDateTime createdAt,
        LocalDateTime publishedAt
) {}
