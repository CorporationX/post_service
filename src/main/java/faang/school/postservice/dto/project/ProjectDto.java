package faang.school.postservice.dto.project;

import lombok.Builder;

@Builder
public record ProjectDto(
    long id,
    String title,
    long ownerId
) {
}
