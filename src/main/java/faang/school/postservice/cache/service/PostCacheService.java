package faang.school.postservice.cache.service;

import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * Сервис для управления кэшем постов, связанных с хэштегами.
 * <p>
 * Предназначен для хранения, удаления и получения постов из кэша (например, Redis),
 * а также для выборки популярных хэштегов.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
public interface PostCacheService {
    /**
     * Добавляет пост в кэш по заданному хэштегу.
     *
     * @param hashtag хэштег, с которым связан пост (включая символ {@code #})
     * @param post    DTO поста для добавления в кэш
     */
    void addPost(String hashtag, PostViewDto post);

    /**
     * Удаляет пост из кэша по заданному хэштегу.
     *
     * @param hashtag хэштег, с которым связан пост (включая символ {@code #})
     * @param post    DTO поста, который необходимо удалить из кэша
     */
    void deletePost(String hashtag, PostViewDto post);

    /**
     * Возвращает список популярных хэштегов в порядке убывания популярности.
     *
     * @param offset смещение для постраничной выборки
     * @param limit  максимальное количество хэштегов в ответе
     * @return список строковых представлений хэштегов
     */
    List<String> getPopularHashtags(long offset, long limit);

    /**
     * Получает список постов, связанных с указанным хэштегом.
     *
     * @param hashtag хэштег (включая символ {@code #})
     * @return список DTO постов, связанных с этим хэштегом
     */
    List<PostViewDto> getList(String hashtag);
}
