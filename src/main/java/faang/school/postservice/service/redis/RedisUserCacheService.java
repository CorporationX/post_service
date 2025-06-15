package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisUserCacheService implements UserCacheService {
    private static final String KEY_PREFIX = "user:";

    private final RedisTemplate<String, String> redis;
    private final ObjectMapper jackson;

    @Value("${cache.user.ttl.minutes}")
    private long userCacheTtl;

    @Override
    public List<UserDto> bulkGet(Collection<Long> userIds, int offset, int limit) {
        List<Long> idsToFetchThisPage = getValidIdsForCurrentPage(userIds, offset, limit);

        if (idsToFetchThisPage.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> keysToFetch = idsToFetchThisPage.stream()
                .filter(Objects::nonNull)
                .map(id -> KEY_PREFIX + id)
                .toList();

        try {
            log.debug("Attempting to fetch {} keys from Redis for offset: {}, limit: {}",
                    keysToFetch.size(), offset, limit);
            List<String> jsonValues = redis.opsForValue().multiGet(keysToFetch);

            if (jsonValues == null) {
                log.warn("Redis multiGet returned null for keys: {}. Assuming no users found for this page.",
                        keysToFetch);
                return Collections.emptyList();
            }

            List<UserDto> result = deserializeUsers(jsonValues, idsToFetchThisPage, keysToFetch);
            log.info("bulkGet completed for page (offset: {}, limit: {}). " +
                            "Retrieved {} users out of {} requested for this page.",
                    offset, limit, result.size(), idsToFetchThisPage.size());
            return result;

        } catch (DataAccessException e) {
            log.error("Redis access error during bulkGet for {} keys (page offset: {}, limit: {}): {}",
                    keysToFetch.size(), offset, limit, e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Unexpected error during bulkGet for {} keys (page offset: {}, limit: {}): {}",
                    keysToFetch.size(), offset, limit, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<Long> getValidIdsForCurrentPage(Collection<Long> userIds, int offset, int limit) {
        if (userIds == null || userIds.isEmpty()) {
            log.debug("Received null or empty userIds for bulkGet. Returning empty list.");
            return Collections.emptyList();
        }
        if (limit <= 0) {
            log.debug("Limit is {} (<=0). Returning empty list.", limit);
            return Collections.emptyList();
        }

        List<Long> nonNullUserIds = userIds.stream()
                .filter(Objects::nonNull)
                .toList();

        if (nonNullUserIds.isEmpty()) {
            log.debug("All provided userIds were null or collection was empty after filtering. " +
                    "Returning empty list.");
            return Collections.emptyList();
        }

        int actualOffset = Math.max(0, offset);

        if (actualOffset >= nonNullUserIds.size()) {
            log.debug("Offset {} is beyond the range of non-null user IDs (size {}). Returning empty list.",
                    actualOffset, nonNullUserIds.size());
            return Collections.emptyList();
        }

        int toIndex = Math.min(actualOffset + limit, nonNullUserIds.size());
        List<Long> idsToFetchThisPage = nonNullUserIds.subList(actualOffset, toIndex);

        if (idsToFetchThisPage.isEmpty()) {
            log.debug("Calculated page of user IDs to fetch is empty. Offset: {}, Limit: {}. Returning empty list.",
                    actualOffset, limit);
            return Collections.emptyList();
        }
        return idsToFetchThisPage;
    }

    private List<UserDto> deserializeUsers(List<String> jsonValues, List<Long> idsInPage,
                                           List<String> keysFetched) {
        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < idsInPage.size(); i++) {
            Long currentId = idsInPage.get(i);
            String json = (i < jsonValues.size()) ? jsonValues.get(i) : null;

            if (json != null) {
                try {
                    UserDto userDto = jackson.readValue(json, UserDto.class);
                    result.add(userDto);
                    log.trace("Successfully deserialized UserDto for ID: {}", currentId);
                } catch (JsonProcessingException e) {
                    log.warn("Failed to deserialize UserDto JSON for ID {}. JSON: '{}'. Error: {}",
                            currentId, jsonToString(json), e.getMessage());
                }
            } else {
                log.debug("No cache entry found in Redis for user ID: {} (key: {}).",
                        currentId, (i < keysFetched.size() ? keysFetched.get(i) : "UNKNOWN_KEY"));
            }
        }
        return result;
    }


    @Override
    public void bulkPut(Collection<UserDto> users) {
        if (users == null || users.isEmpty()) {
            log.debug("User collection for bulkPut is null or empty. Skipping operation.");
            return;
        }

        final Duration ttl = Duration.ofMinutes(userCacheTtl);
        if (ttl.isNegative() || ttl.isZero()) {
            log.error("Invalid TTL ({} minutes) for bulkPut operation. Aborting operation. TTL must be positive.",
                    userCacheTtl);
            return;
        }

        final Expiration expiration = Expiration.from(ttl);
        try {
            List<Object> pipeLineResults =
                    redis.executePipelined((RedisCallback<Object>) connection -> {
                        preparePipelineOperations(connection, users, expiration);
                        return null;
                    });
            log.info("Pipelined bulkPut operation submitted for approximately {} users with TTL {}. " +
                            "Pipeline results count: {}",
                    users.size(), ttl, pipeLineResults.size());
        } catch (DataAccessException e) {
            log.error("Redis access error during pipelined bulkPut execution for {} users: {}",
                    users.size(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during bulkPut operation for {} users: {}",
                    users.size(), e.getMessage(), e);
        }
    }

    private void preparePipelineOperations(RedisConnection connection, Collection<UserDto> users,
                                           Expiration expiration) {
        RedisSerializer<String> keySerializer = redis.getStringSerializer();
        RedisSerializer<String> valueSerializer = redis.getStringSerializer();
        int usersPrepared = 0;

        for (UserDto user : users) {
            if (user == null || user.id() == null) {
                log.warn("Skipping null user or user with null ID in bulkPut pipeline preparation.");
                continue;
            }
            String key = KEY_PREFIX + user.id();
            try {
                String jsonValue = jackson.writeValueAsString(user);
                byte[] rawKey = keySerializer.serialize(key);
                byte[] rawValue = valueSerializer.serialize(jsonValue);

                if (rawKey != null && rawValue != null) {
                    connection.stringCommands().set(rawKey,
                            rawValue,
                            expiration,
                            RedisStringCommands.SetOption.UPSERT);
                    usersPrepared++;
                    log.trace("Added user ID {} to pipeline for caching with TTL {}.",
                            user.id(), expiration);
                } else {
                    log.warn("Skipping user ID {} due to null serialized key/value before pipelining.",
                            user.id());
                }
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize UserDto to JSON for ID {} during bulkPut pipeline preparation: {}",
                        user.id(), e.getMessage(), e);
            } catch (Exception e) {
                log.error("Unexpected error processing user ID {} for bulkPut pipeline {}: {}",
                        user.id(), key, e.getMessage(), e);
            }
        }
        log.debug("Prepared {} users for pipelined set operation.", usersPrepared);
    }

    private String jsonToString(String json) {
        int maxLength = 200;
        if (json == null) {
            return "null";
        }
        if (json.length() > maxLength) {
            return json.substring(0, maxLength) + "...";
        }
        return json;
    }
}
