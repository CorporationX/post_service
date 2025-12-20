package faang.school.postservice.dto.comment;

import lombok.Builder;

@Builder
public record PublishCommentDto(
        Long commentId,
        Long authorId
) {
}