package faang.school.postservice.kafka.events;

import java.util.List;

public record FeedDto(
        Long followerId,
        List<Long> posts
) {}