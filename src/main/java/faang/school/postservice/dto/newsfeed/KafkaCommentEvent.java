package faang.school.postservice.dto.newsfeed;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record KafkaCommentEvent (

        Long commentId,
        Long postId,
        Long userId,
        Long authorId,
        String content,
        LocalDateTime createdAt
) {}
