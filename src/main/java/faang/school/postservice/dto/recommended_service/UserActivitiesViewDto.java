package faang.school.postservice.dto.recommended_service;

import java.util.List;
import java.util.Map;

/**
 * DTO с активностями пользователя
 *
 * @author Linempy
 * @since 23.11.2025
 */
public record UserActivitiesViewDto(
        Map<Long, Map<String, List<String>>> activities
) {
}