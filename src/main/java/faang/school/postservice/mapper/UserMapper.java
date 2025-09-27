package faang.school.postservice.mapper;

import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import org.springframework.stereotype.Component;

/**
 * Маппер для получения объекта {@link UserRedisDto} для сохранения в Redis.
 * Объект {@code UserRedisDto} используется для формирования Feed
 *
 * @author Linempy
 * @since 27.09.2025
 */
@Component
public class UserMapper {

    public UserRedisDto toRedisDto(UserViewDto userViewDto) {
        return new UserRedisDto(userViewDto.id(), userViewDto.username());
    }
}