package faang.school.postservice.kafka.model;

public record LikeEvent(
        Long authorLikeId,
        Long postId,
        Long commentId
) {}
