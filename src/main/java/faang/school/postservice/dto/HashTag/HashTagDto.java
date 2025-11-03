package faang.school.postservice.dto.HashTag;

import faang.school.postservice.dto.project.ProjectDto;

import java.time.LocalDateTime;

public record HashTagDto(
        Long id,
        String content,
        ProjectDto projectDto,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
