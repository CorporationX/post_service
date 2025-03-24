package faang.school.postservice.dto.project;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ProjectDto(
        long id,
        String name,
        String description,
        long ownerId,
        ProjectStatus status,
        ProjectVisibility visibility,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {
}
