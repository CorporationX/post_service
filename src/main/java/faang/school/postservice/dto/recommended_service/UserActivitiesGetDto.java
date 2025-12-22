package faang.school.postservice.dto.recommended_service;

import java.util.List;

/**
 * DTO для post запроса на получение активностей пользователя
 *
 * @param userIds список id пользователей, для которых получаем активности
 * @param limit ограничение на кол-во активностей
 * @author Linempy
 * @since 23.11.2025
 */
public record UserActivitiesGetDto(
        List<Long> userIds,
        Integer limit
) {
}