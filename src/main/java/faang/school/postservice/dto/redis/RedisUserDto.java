package faang.school.postservice.dto.redis;

import faang.school.postservice.model.UserProfilePic;

public record RedisUserDto(
        long id,
        UserProfilePic picture
) {
}
