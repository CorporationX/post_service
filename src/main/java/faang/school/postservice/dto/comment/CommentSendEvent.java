package faang.school.postservice.dto.comment;

import lombok.Builder;

@Builder
public record CommentSendEvent(Long postId, Long commentId, Long authorId) {
}
