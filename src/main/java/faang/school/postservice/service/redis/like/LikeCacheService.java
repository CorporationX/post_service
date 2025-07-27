package faang.school.postservice.service.redis.like;

import faang.school.postservice.dto.LikeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Defines the contract for caching services related to "likes" on entities
 * (e.g., posts or comments).
 * <p>
 * This service is responsible for managing cached data such as:
 * <ul>
 *     <li>User IDs of users who liked an entity, stored in a way that allows pagination (e.g., Redis ZSET).</li>
 *     <li>Total count of likes for an entity.</li>
 * </ul>
 * It also handles interactions with the primary data source (database) when cache misses occur
 * or when the cache needs to be populated.
 * </p>
 * Implementations of this interface will be specific to the type of entity being liked
 * (e.g., {@code RedisPostLikeCacheService}, {@code RedisCommentLikeCacheService}).
 */
public interface LikeCacheService {
    /**
     * "Touches" the cache keys associated with a given entity ID, typically by resetting their
     * Time-To-Live (TTL). This is used to keep frequently accessed cache entries alive.
     * If the keys do not exist, this operation might do nothing.
     *
     * @param entityId The unique identifier of the entity (e.g., post ID, comment ID)
     *                 whose cache entries are to be touched. Must not be null.
     */
    void touchKeys(Long entityId);

    /**
     * Retrieves a paginated list of user IDs from the cache for a given entity.
     * These user IDs represent users who have liked the entity.
     * The order is typically based on the time of the like (e.g., most recent first).
     *
     * @param entityId The unique identifier of the entity. Must not be null.
     * @param offset The starting position (0-based index) for the page of user IDs.
     * @param limit The maximum number of user IDs to retrieve for the page.
     * @return A {@link List} of user IDs. Returns an empty list if no user IDs are found
     *         in the cache for the given entity and pagination parameters, or if input is invalid.
     */
    List<Long> getUserIdsPage(Long entityId, int offset, int limit);

    /**
     * Fetches the total count of likes for a given entity directly from the primary data source (database).
     * This method is typically called during cache misses or when populating the cache.
     *
     * @param entityId The unique identifier of the entity. Must not be null.
     * @return The total number of likes as recorded in the database.
     * @throws IllegalArgumentException if entityId is null (behavior may vary by implementation).
     */
    long fetchTotalLikesCountFromDb(Long entityId);

    /**
     * Fetches a paginated list of {@link LikeDto} objects for a given entity
     * directly from the primary data source (database).
     * This is used to retrieve detailed like information when populating or refreshing the cache.
     *
     * @param entityId The unique identifier of the entity. Must not be null.
     * @param pageable Pagination information (page number, size, sort order).
     * @return A {@link Page} of {@link LikeDto} objects.
     * @throws IllegalArgumentException if entityId is null (behavior may vary by implementation).
     */
    Page<LikeDto> fetchLikesPageFromDb(Long entityId, Pageable pageable);

    /**
     * Populates or updates the cache with a window of like data and the total like count,
     * typically after fetching this information from the database.
     * <p>
     * This method is responsible for storing:
     * <ul>
     *     <li>The provided {@code likesWindow} (usually user IDs and their like timestamps)
     *         in a structure that allows paginated retrieval (e.g., Redis ZSET).</li>
     *     <li>The {@code totalLikesFromDb} count.</li>
     * </ul>
     * Cache entries should be set with an appropriate Time-To-Live (TTL).
     *
     * @param entityId The unique identifier of the entity for which to cache likes. Must not be null.
     * @param likesWindow A list of {@link LikeDto} objects representing a segment of likes
     *                    (e.g., a pre-fetched window) to be stored in the cache. Can be empty or null.
     * @param totalLikesFromDb The authoritative total number of likes for the entity, fetched from the database.
     */
    void populateLikesCacheFromDb(Long entityId, List<LikeDto> likesWindow, long totalLikesFromDb);

    /**
     * Returns the name of the entity this cache service is responsible for (e.g., "post", "comment").
     * This is often used for logging and constructing cache key prefixes.
     *
     * @return A string representing the entity name.
     */
    String getEntityName();

    /**
     * Generates or retrieves the cache key used for storing the total count of likes for a given entity.
     *
     * @param entityId The unique identifier of the entity. Can be null, in which case a default
     *                 key or behavior might be defined by the implementation.
     * @return The cache key string for the likes count.
     */
    String cntKey(Long entityId);

    /**
     * Generates or retrieves the cache key used for storing the sorted set of user IDs (or like timestamps)
     * for a given entity. This set is typically used for paginated retrieval of likers.
     *
     * @param entityId The unique identifier of the entity. Can be null, in which case a default
     *                 key or behavior might be defined by the implementation.
     * @return The cache key string for the sorted set of likes.
     */
    String zsetKey(Long entityId);

    /**
     * Retrieves the total count of likes for a given entity, attempting to fetch from the cache first.
     * If the count is not found in the cache (cache miss), it should fall back to fetching
     * from the database (e.g., by calling {@link #fetchTotalLikesCountFromDb(Long)}) and then
     * cache the result.
     *
     * @param entityId The unique identifier of the entity. Must not be null.
     * @return The total number of likes. Returns 0 if the entityId is null or if no likes are found.
     */
    long getTotalLikesCount(Long entityId);
}
