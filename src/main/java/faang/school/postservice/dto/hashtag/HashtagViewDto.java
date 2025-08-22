package faang.school.postservice.dto.hashtag;

/**
 * DTO для представления хэштега.
 * <p>
 * Используется для передачи информации о хэштеге во внешние системы
 * или в слой представления (например, в REST API).
 * </p>
 *
 * @param id   уникальный идентификатор хэштега
 * @param name название хэштега (включая символ {@code #} или без него,
 *             в зависимости от контекста использования)
 * @author Myrza
 * @since 08.08.2025
 */
public record HashtagViewDto(
        Long id,
        String name
) {
}
