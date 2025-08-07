package faang.school.postservice.dto.hashtag;

/**
 * HashtagViewDto — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
 *
 * @param name описание первого поля
 * @author Myrza
 * @since 08.08.2025
 */
public record HashtagViewDto(
        Long id,
        String name
) {
}
