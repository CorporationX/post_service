package faang.school.postservice.dto.resource;

import faang.school.postservice.model.resource.ResourceType;

import java.time.LocalDateTime;

public record ResourceDto(
        Long id,
        String key,
        String name,
        long size,
        ResourceType type,
        LocalDateTime createdAt,
        Long postId
) {}
