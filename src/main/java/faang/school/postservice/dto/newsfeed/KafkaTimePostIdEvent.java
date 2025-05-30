package faang.school.postservice.dto.newsfeed;

import lombok.Builder;

@Builder
public record KafkaTimePostIdEvent(

        Long id,
        Long publishedAt
) {}
