package faang.school.postservice.dto.newsfeed;

import lombok.Builder;

@Builder
public record KafkaLikeEvent (

        Long postId,
        Long userId,
        Long authorId,
        Long timestamp
) {}
