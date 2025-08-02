package faang.school.postservice.dto.post;

import faang.school.postservice.model.enums.AuthorType;

/**
 * Класс, содержащий информацию об авторе поста
 *
 * @param type - тип автора (USER, PROJECT)
 * @param id - идентификатор соответствующего автора
 *
 * @author Linempy
 * @since 01.08.2025
 */
public record AuthorFilter(
        AuthorType type,
        Long id
) {
}