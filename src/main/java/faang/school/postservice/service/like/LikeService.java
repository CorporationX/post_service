package faang.school.postservice.service.like;

/**
 * Сервис для управления лайками под постами и комментариями.
 * <p>
 * Обеспечивает бизнес-логику добавления и удаления лайков.
 * Проверяет существование пользователей, постов и комментариев,
 * а также запрещает ставить лайк одновременно и посту, и комментарию,
 * а также повторные лайки от одного пользователя на один объект.
 * </p>
 * <p>
 * Методы принимают идентификаторы постов или комментариев,
 * а пользователя берут из контекста безопасности.
 * </p>
 *
 * @author agent
 * @since 12.08.2025
 */
public interface LikeService {

    /**
     * Добавляет лайк текущего пользователя к посту с указанным ID.
     *
     * @param postId ID поста для лайка
     * @throws IllegalStateException если лайк уже поставлен или нарушены бизнес-правила
     */
    void addLikeToPost(long postId);

    /**
     * Удаляет лайк текущего пользователя с поста с указанным ID.
     *
     * @param postId ID поста, с которого нужно удалить лайк
     * @throws IllegalStateException если лайк не найден
     */
    void removeLikeFromPost(long postId);

    /**
     * Добавляет лайк текущего пользователя к комментарию с указанным ID.
     *
     * @param commentId ID комментария для лайка
     * @throws IllegalStateException если лайк уже поставлен или нарушены бизнес-правила
     */
    void addLikeToComment(long commentId);

    /**
     * Удаляет лайк текущего пользователя с комментария с указанным ID.
     *
     * @param commentId ID комментария, с которого нужно удалить лайк
     * @throws IllegalStateException если лайк не найден
     */
    void removeLikeFromComment(long commentId);
}