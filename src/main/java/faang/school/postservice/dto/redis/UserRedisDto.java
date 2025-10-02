package faang.school.postservice.dto.redis;

/**
 * DTO для сохранения в Redis
 *
 * @param id идентификатор пользователя
 * @param username имя пользователя
 *
 * @author Linempy
 * @since 27.09.2025
 */
public record UserRedisDto(
        Long id,
        String username
) {
}