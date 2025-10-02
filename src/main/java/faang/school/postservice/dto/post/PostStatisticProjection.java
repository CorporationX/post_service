package faang.school.postservice.dto.post;

/**
 * Проекция для получения кол-ва лайков и комментариев из БД
 *
 * @author Linempy
 * @since 27.09.2025
 */

public interface PostStatisticProjection {
    Long getLikeCount();

    Long getCommentCount();
}