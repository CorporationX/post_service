package faang.school.postservice.model.redis;

import faang.school.postservice.dto.user.UserDto;
import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;


@RedisHash(value = "UserInCache", timeToLive = 60 * 60 * 24 * 7)
@Builder
public class UserForRedis {
    // хранения авторов поста в кэше
    // id пользователя(автора поста), сам пользователь с необходимыми данными из базы
    @Id
    private long id;
    private UserDto user;
}
