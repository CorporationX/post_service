package faang.school.postservice.dto.redis;

import faang.school.postservice.model.UserProfilePic;
import lombok.Getter;

@Getter
public class RedisUserDto {
    long id;
    UserProfilePic picture;
}
