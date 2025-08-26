package faang.school.postservice.cache.service.redis;

import faang.school.postservice.cache.service.PostCacheService;
import faang.school.postservice.dto.post.PostViewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Реализация {@link PostCacheService} для работы с кэшем постов и статистикой хэштегов в Redis.
 * <p>
 * Использует:
 * <ul>
 *     <li>{@link StringRedisTemplate} — для хранения и обновления счётчиков популярности хэштегов
 *         в отсортированном множестве Redis.</li>
 *     <li>{@link RedisTemplate} — для хранения списков постов, связанных с конкретным хэштегом.</li>
 * </ul>
 * <p>
 * Класс поддерживает добавление, удаление и выборку постов, а также определение популярных хэштегов.
 * Ограничивает количество хранимых постов по каждому хэштегу до {@value #MAX_POSTS_COUNT}.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {
    private static final String HASHTAG_COUNT_KEY = "hashtag_count";
    private static final long MAX_POSTS_COUNT = 50;
    private static final int FIRST_INDEX = 0;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, List<PostViewDto>> redisTemplate;

    /**
     * Добавляет пост в кэш для заданного хэштега и увеличивает его популярность.
     * Если постов больше {@value #MAX_POSTS_COUNT}, удаляется самый старый.
     *
     * @param hashtag хэштег, с которым связан пост
     * @param post    объект поста
     */
    @Override
    public void addPost(String hashtag, PostViewDto post) {
        List<PostViewDto> posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            posts = new LinkedList<>();
        }
        posts.add(post);
        if (posts.size() > MAX_POSTS_COUNT) {
            posts.remove(FIRST_INDEX);
        }
        redisTemplate.opsForValue().set(hashtag, posts);
        stringRedisTemplate.opsForZSet().incrementScore(HASHTAG_COUNT_KEY, hashtag, 1);
        log.info("успешно создали связь между постом с id {} и хэштега '{}'", post.id(), hashtag);
    }

    /**
     * Удаляет пост из кэша по заданному хэштегу и уменьшает его популярность.
     * Если пост не найден, выводит предупреждение в лог.
     *
     * @param hashtag хэштег, с которым связан пост
     * @param post    объект поста для удаления
     */
    @Override
    public void deletePost(String hashtag, PostViewDto post) {
        List<PostViewDto> posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            return;
        }

        var success = posts.remove(post);
        if (!success) {
            log.warn("удаление связи поста с id {} и хэштега '{}' не удалась", post.id(), hashtag);
            return;
        }
        redisTemplate.opsForValue().set(hashtag, posts);
        stringRedisTemplate.opsForZSet().incrementScore(HASHTAG_COUNT_KEY, hashtag, -1);
        log.info("успешно удалили связь поста с id {} и хэштегом '{}'", post.id(), hashtag);
    }

    /**
     * Возвращает список популярных хэштегов по убыванию популярности.
     *
     * @param offset смещение в выборке
     * @param limit  максимальное количество хэштегов
     * @return список хэштегов в порядке популярности
     */
    @Override
    public List<String> getPopularHashtags(long offset, long limit) {
        var end = offset + limit - 1;
        Set<ZSetOperations.TypedTuple<String>> hashtags = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(HASHTAG_COUNT_KEY, offset, end);
        if (hashtags == null) {
            return Collections.emptyList();
        }
        return hashtags.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .toList();
    }

    /**
     * Получает список постов, связанных с указанным хэштегом.
     *
     * @param hashtag хэштег, для которого запрашиваются посты
     * @return список постов, либо пустой список, если посты не найдены
     */
    @Override
    public List<PostViewDto> getList(String hashtag) {
        var posts = redisTemplate.opsForValue().get(hashtag);
        if (posts == null) {
            return Collections.emptyList();
        }

        return posts;
    }

}
