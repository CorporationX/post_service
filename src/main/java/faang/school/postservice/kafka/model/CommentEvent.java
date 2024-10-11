package faang.school.postservice.kafka.model;

public record CommentEvent(
        Long id,
        String content,
        Long postId
) {}
