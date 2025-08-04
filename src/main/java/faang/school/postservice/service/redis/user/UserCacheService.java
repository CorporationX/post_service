package faang.school.postservice.service.redis.user;

import faang.school.postservice.dto.user.UserDto;

import java.util.Collection;
import java.util.List;

/**
 * Service interface for managing a cache of {@link UserDto} objects.
 * <p>
 * This service provides functionalities to:
 * <ul>
 *     <li>Retrieve multiple {@link UserDto} objects from the cache in a single operation (bulk get).</li>
 *     <li>Store multiple {@link UserDto} objects into the cache (bulk put).</li>
 * </ul>
 */
public interface UserCacheService {

    /**
     * Retrieves a list of {@link UserDto} objects from the cache based on a collection of user IDs.
     * <p>
     * This method supports pagination over the input {@code userIds} collection.
     * It attempts to fetch users corresponding to the IDs within the specified page (offset and limit)
     * from the cache.
     *
     * @param userIds A collection of unique identifiers for the users to retrieve.
     *                Null or empty collection will result in an empty list.
     *                Null elements within the collection will be ignored.
     * @param offset The starting index (0-based) within the filtered {@code userIds} list
     *               from which to retrieve users.
     * @param limit The maximum number of users to retrieve for the current page.
     *              If {@code limit} is non-positive, an empty list may be returned.
     * @return A {@link List} of {@link UserDto} objects found in the cache for the specified page of user IDs.
     *         The list will only contain users that were found in the cache.
     *         The order of users in the returned list corresponds to the order of their IDs
     *         in the input {@code userIds} collection, after pagination is applied.
     *         Returns an empty list if no users are found for the given page,
     *         if {@code userIds} is null/empty, or if pagination parameters are invalid.
     */
    List<UserDto> bulkGet(Collection<Long> userIds, int offset, int limit);

    /**
     * Stores a collection of {@link UserDto} objects into the cache.
     * <p>
     * Each user DTO in the collection will be cached, typically using its ID as part of the cache key.
     * Existing entries for the same user IDs may be overwritten.
     * Implementations should handle setting an appropriate Time-To-Live (TTL) for the cached entries.
     *
     * @param users A collection of {@link UserDto} objects to store in the cache.
     *              If the collection is null or empty, the operation will be skipped.
     *              Null {@link UserDto} objects or users with null IDs within the collection may be ignored.
     */
    void bulkPut(Collection<UserDto> users);
}
