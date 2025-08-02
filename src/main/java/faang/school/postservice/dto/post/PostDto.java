package faang.school.postservice.dto.post;

import lombok.Builder;

@Builder
public record PostDto(
    Long id,
    String content,
    Long authorId,
    Long projectId
) {
}
