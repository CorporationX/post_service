package faang.school.postservice.dto.project;

import java.util.List;

public record ProjectDto(
    long id,
    String title,
    List<Long> participants
) {
}
