package faang.school.postservice.dto.comment;

import java.time.Instant;

/**
 * CommentPublishedEvent — dto для передачи ивента создания комментария.
 *
 * @author bozya
 * @since 26.09.2025
 */

public record CommentCreatedEvent(
        Long commentId,
        Long postId,
        Long authorId,
        String content,
        Instant createdAt
) {}