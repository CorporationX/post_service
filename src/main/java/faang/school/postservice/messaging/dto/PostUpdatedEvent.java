package faang.school.postservice.messaging.dto;

import faang.school.postservice.dto.post.PostViewDto;

/**
 * PostUpdateDto — неизменяемая структура данных (record).
 * <p>
 * TODO: описать предназначение record и его поля.
 * </p>
 *
 * @param oldPost описание первого поля
 * @author Myrza
 * @since 09.08.2025
 */
public record PostUpdatedEvent(
        PostViewDto oldPost,
        PostViewDto newPost
) {
}
