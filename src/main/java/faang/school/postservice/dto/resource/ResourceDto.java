package faang.school.postservice.dto.resource;

import java.time.LocalDateTime;

public record ResourceDto(
        Long id,
        String key,
        Long size,
        String name,
        String type,
        LocalDateTime createdAt,
        Long postId
) {
}
