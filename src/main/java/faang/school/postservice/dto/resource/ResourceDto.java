package faang.school.postservice.dto.resource;

import lombok.Builder;

@Builder
public record ResourceDto(
        Long id,
        String key,
        Long size,
        String name,
        String type,
        Long postId
) {
}