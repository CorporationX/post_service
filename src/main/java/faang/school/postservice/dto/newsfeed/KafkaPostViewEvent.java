package faang.school.postservice.dto.newsfeed;

import lombok.Builder;

@Builder
public record KafkaPostViewEvent (

        Long postId,
        Long userId,
        Long authorId,
        Long viewTimestamp
) {}
