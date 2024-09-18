package faang.school.postservice.dto;

import java.util.List;

public record PostDto(long id, Long authorId, List<Long> likes) {
}
