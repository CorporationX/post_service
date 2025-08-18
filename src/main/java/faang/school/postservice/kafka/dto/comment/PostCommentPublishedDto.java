package faang.school.postservice.kafka.dto.comment;

import lombok.Builder;

@Builder
public record PostCommentPublishedDto(
        long id,
        long postId,
        long authorId
) {
}
