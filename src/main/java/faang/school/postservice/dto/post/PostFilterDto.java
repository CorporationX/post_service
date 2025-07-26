package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO для фильтрации публикаций.
 * Используется для задания критериев при получении списка постов.
 *
 * @param authorId     Идентификатор автора.
 *                     Должен быть положительным числом.
 * @param authorIsUser Флаг, указывающий, опубликовал ли публикацию
 *                     пользователь или проект
 * @param published    Флаг, указывающий, опубликованы ли публикации.
 * @author Myrza
 * @since 24.07.2025
 */
public record PostFilterDto(
        @Positive
        Long authorId,
        @NotNull
        Boolean authorIsUser,
        @NotNull
        Boolean published
) {
}
