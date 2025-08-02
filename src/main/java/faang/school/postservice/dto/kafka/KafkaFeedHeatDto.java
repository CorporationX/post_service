package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.feed.UserFeedHeatDto;

import java.util.List;

public record KafkaFeedHeatDto(
        List<UserFeedHeatDto> users
) {
}
