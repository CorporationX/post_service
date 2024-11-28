package faang.school.postservice.dto.kafka.event;

import lombok.Builder;



@Builder
public record CommentEventDto (
    long commentId,
    long authorId,
    long postId
){}
