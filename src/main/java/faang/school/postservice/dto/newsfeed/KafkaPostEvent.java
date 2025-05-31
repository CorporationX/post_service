package faang.school.postservice.dto.newsfeed;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record KafkaPostEvent (

        Long postId,
        Long userId,
        String content,
        LocalDateTime publishedAt,
        List<Long> subscribersIds
) {}
