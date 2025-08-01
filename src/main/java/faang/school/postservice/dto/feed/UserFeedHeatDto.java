package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.RedisUserDto;

import java.util.List;

public record UserFeedHeatDto(
    RedisUserDto user,
    List<Long> followerIds
) {
}
