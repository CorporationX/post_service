package faang.school.postservice.service.redis;

import faang.school.postservice.dto.LikeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class RedisLikeCacheServiceAbstract implements LikeCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final String entityName = keyPrefix().substring(0, keyPrefix().indexOf(":"));

    @Value("${cache.like.ttl.minutes}")
    private long likesCacheTtl;

    @Override
    public abstract String zsetKey(Long entityId);

    @Override
    public abstract String cntKey(Long entityId);

    @Override
    public abstract long fetchTotalLikesCountFromDb(Long entityId);

    @Override
    public abstract Page<LikeDto> fetchLikesPageFromDb(Long entityId, Pageable pageable);

    protected abstract String keyPrefix();

    @Override
    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public List<Long> getUserIdsPage(Long entityId, int offset, int limit) {
        if (entityId == null) {
            log.warn("getUserIdsPage called with null {}Id.", entityName);
            return Collections.emptyList();
        }
        if (offset < 0 || limit <= 0) {
            log.warn("getUserIdsPage called with invalid offset ({}) or limit ({}). Returning empty list.",
                    offset, limit);
            return Collections.emptyList();
        }

        String key = zsetKey(entityId);
        try {
            Set<String> userIdsAsStrings = redisTemplate.opsForZSet().reverseRange(
                    key, offset, (long) offset + limit - 1);

            if (userIdsAsStrings == null || userIdsAsStrings.isEmpty()) {
                log.debug("Cache miss or empty list for {} {} with offset {} and limit {}. Key: {}",
                        entityName, entityId, offset, limit, key);
                return Collections.emptyList();
            }

            return userIdsAsStrings.stream()
                    .map(s -> {
                        try {
                            return Long.valueOf(s);
                        } catch (NumberFormatException e) {
                            log.warn("Invalid user ID format '{}' in cache for key {}.", s, key);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Redis access error retrieving user IDs page for {} {}. Key: {}. Error: {}",
                    entityName, entityId, key, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void populateLikesCacheFromDb(Long entityId, List<LikeDto> likesWindow, long totalLikesFromDb) {
        if (entityId == null) {
            log.error("populateLikesCacheFromDb: Cannot cache for a null {}Id.", entityName);
            return;
        }

        log.info("Populating L1 cache for {} ID {} with a window of {} likes and total count {}.",
                entityName, entityId, likesWindow != null ? likesWindow.size() : 0, totalLikesFromDb);

        int populatedCount = populateUserIdsZset(entityId, likesWindow);
        if (populatedCount <= 0) {
            log.warn("populateUserIdsZset returned 0 for {} ID {}. No user IDs were added to the ZSET.",
                    entityName, entityId);
        }

        String countCacheKey = cntKey(entityId);
        try {
            redisTemplate.opsForValue().set(
                    countCacheKey,
                    String.valueOf(totalLikesFromDb),
                    Duration.ofMinutes(likesCacheTtl)
            );
            log.info("Set total likes count ({}) in L1 cache for {} ID {}. Count Key: {}, TTL: {}m",
                    totalLikesFromDb, entityName, entityId, countCacheKey, likesCacheTtl);
        } catch (DataAccessException e) {
            log.error("Redis error setting count key {} for {} ID {} during L1 cache population. Error: {}",
                    countCacheKey, entityName, entityId, e.getMessage(), e);
        }
    }

    @Override
    public long getTotalLikesCount(Long entityId) {
        if (entityId == null) {
            log.warn("getTotalLikesCount called with null {}Id.", getEntityName());
            return 0;
        }
        String countKey = cntKey(entityId);
        try {
            String countStr = redisTemplate.opsForValue().get(countKey);
            if (countStr != null) {
                try {
                    log.debug("Cache HIT for total likes count for {} {}. Key: {}", getEntityName(),
                            entityId, countKey);
                    return Long.parseLong(countStr);
                } catch (NumberFormatException e) {
                    log.warn("Invalid count format '{}' in cache for key {}. Fetching from DB.",
                            countStr, countKey);
                }
            }
            log.debug("Cache MISS for total likes count for {} {}. Key: {}. Fetching from DB.",
                    getEntityName(), entityId, countKey);

            long totalLikesFromDb = fetchTotalLikesCountFromDb(entityId);
            redisTemplate.opsForValue().set(
                    countKey,
                    String.valueOf(totalLikesFromDb),
                    Duration.ofMinutes(likesCacheTtl)
            );
            log.info("Fetched and cached total likes count ({}) for {} ID: {}. Count Key: {}",
                    totalLikesFromDb, getEntityName(), entityId, countKey);
            return totalLikesFromDb;
        } catch (DataAccessException e) {
            log.error("Redis error getting total likes count for {} {}. Key: {}. Error: {}. Falling back to DB.",
                    getEntityName(), entityId, countKey, e.getMessage(), e);
            return fetchTotalLikesCountFromDb(entityId);
        } catch (Exception e) {
            log.error("Unexpected error getting total likes count for {} {}. Key: {}. Error: {}. Falling back to DB.",
                    getEntityName(), entityId, countKey, e.getMessage(), e);
            return fetchTotalLikesCountFromDb(entityId);
        }
    }

    @Override
    public void touchKeys(Long entityId) {
        if (entityId == null) {
            log.warn("Cannot touch keys for null {}Id.", entityName);
            return;
        }
        String sortedSetCacheKey = zsetKey(entityId);
        String countCacheKey = cntKey(entityId);
        Duration ttlDuration = Duration.ofMinutes(likesCacheTtl);

        try {
            if (redisTemplate.hasKey(sortedSetCacheKey)) {
                redisTemplate.expire(sortedSetCacheKey, ttlDuration);
                log.debug("Touched ZSET key {} for {} Id {}", sortedSetCacheKey, entityName, entityId);
            } else {
                log.debug("ZSET key {} for {} Id {} does not exist, not touching.",
                        sortedSetCacheKey, entityName, entityId);
            }

            if (redisTemplate.hasKey(countCacheKey)) {
                redisTemplate.expire(countCacheKey, ttlDuration);
                log.debug("Touched count key {} for {} Id {}", countCacheKey, entityName, entityId);
            } else {
                log.debug("Count key {} for {} Id {} does not exist, not touching.",
                        countCacheKey, entityName, entityId);
            }
        } catch (DataAccessException e) {
            log.error("Redis error touching keys for {}Id {}. ZSetKey: {}, CountKey:{}. Error: {}",
                    entityName, entityId, sortedSetCacheKey, countCacheKey, e.getMessage(), e);
        }
    }

    public void cacheTotalLikesCount(Long entityId) {
        if (entityId == null) {
            log.error("Cannot cache total likes count for a null {}Id.", entityName);
            return;
        }
        String countKey = cntKey(entityId);
        log.info("Attempting to fetch and cache total likes count for {} ID: {}. Count Key: {}",
                entityName, entityId, countKey);
        long totalLikes;
        try {
            totalLikes = fetchTotalLikesCountFromDb(entityId);
            log.debug("Fetched total likes count for {} ID {}: {}", entityName, entityId, totalLikes);
        } catch (Exception e) {
            log.error("Failed to fetch total likes count from DB for {} ID: {}. Error: {}",
                    entityName, entityId, e.getMessage(), e);
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    countKey,
                    String.valueOf(totalLikes),
                    Duration.ofMinutes(likesCacheTtl));
            log.info("Successfully cached total likes count ({}) for {} ID: {} and set TTL. Count Key: {}",
                    totalLikes, entityName, entityId, countKey);
        } catch (DataAccessException e) {
            log.error("Redis access error caching total likes count for {} {}. Count Key: {}. Error: {}",
                    entityName, entityId, countKey, e.getMessage(), e);
        }
    }

    public void addLikeToCache(Long entityId, Long userId, long timestamp) {
        if (entityId == null || userId == null) {
            log.warn("addLikeToCache called with null {}Id ({}) or userId ({}). Aborting.",
                    entityName, entityId, userId);
            return;
        }
        String sortedSetKey = zsetKey(entityId);
        String countKey = cntKey(entityId);
        Duration ttl = Duration.ofMinutes(likesCacheTtl);
        try {
            Boolean added = redisTemplate.opsForZSet().add(sortedSetKey, String.valueOf(userId), (double) timestamp);
            redisTemplate.expire(sortedSetKey, ttl);

            if (Boolean.TRUE.equals(added)) {
                if (redisTemplate.hasKey(countKey)) {
                    redisTemplate.opsForValue().increment(countKey);
                    redisTemplate.expire(countKey, ttl);
                } else {
                    getTotalLikesCount(entityId);
                    cacheTotalLikesCount(entityId);
                }
                log.info("Added like from user {} to {} {} (ZSET Key: {}). Incremented/set count (Count Key: {}).",
                        userId, entityName, entityId, sortedSetKey, countKey);
            } else {
                log.info("Updated like timestamp for user {} on {} {} (ZSET Key: {}). Count not changed.",
                        userId, entityName, entityId, sortedSetKey);
            }
        } catch (DataAccessException e) {
            log.error("Redis error adding like for {} {}, user {}. ZSetKey: {}, CountKey: {}. Error: {}",
                    entityName, entityId, userId, sortedSetKey, countKey, e.getMessage(), e);
        }
    }

    public void removeLikeFromCache(Long entityId, Long userId) {
        if (entityId == null || userId == null) {
            log.warn("removeLikeFromCache called with null {}Id ({}) or userId ({}). Aborting.",
                    entityName, entityId, userId);
            return;
        }
        String sortedSetKey = zsetKey(entityId);
        String countKey = cntKey(entityId);
        Duration ttl = Duration.ofMinutes(likesCacheTtl);
        try {
            Long removedCount = redisTemplate.opsForZSet().remove(sortedSetKey, String.valueOf(userId));
            if (removedCount != null && removedCount > 0) {
                redisTemplate.expire(sortedSetKey, ttl);
                if (redisTemplate.hasKey(countKey)) {
                    redisTemplate.opsForValue().decrement(countKey);
                    redisTemplate.expire(countKey, ttl);
                } else {
                    cacheTotalLikesCount(entityId);
                }
                log.info("Removed like from user {} for {} {} (ZSET Key: {}). Decremented/set count (Count Key: {}).",
                        userId, entityName, entityId, sortedSetKey, countKey);
            } else {
                log.debug("Like from user {} for {} {} not found in ZSET cache for removal. Keys: {}, {}",
                        userId, entityName, entityId, sortedSetKey, countKey);
            }
        } catch (DataAccessException e) {
            log.error("Redis error removing like for {} {}, user {}. ZSetKey: {}, CountKey: {}. Error: {}",
                    entityName, entityId, userId, sortedSetKey, countKey, e.getMessage(), e);
        }
    }

    public int populateUserIdsZset(Long entityId, List<LikeDto> likesToCache) {
        if (entityId == null) {
            log.error("Cannot populate likes ZSET cache for a null {}Id.", entityName);
            return 0;
        }
        String key = zsetKey(entityId);
        log.debug("Attempting to populate likes ZSET cache for {} ID: {}. Key: {}", entityName, entityId, key);

        if (likesToCache == null || likesToCache.isEmpty()) {
            log.info("No likes provided to populate ZSET cache for {} ID: {}. Clearing existing ZSET key {}.",
                    entityName, entityId, key);
            try {
                redisTemplate.delete(key);
            } catch (DataAccessException e) {
                log.error("Error clearing Redis ZSET key {} for empty likes list. {}Id: {}. Error: {}",
                        key, entityName, entityId, e.getMessage(), e);
            }
            return 0;
        }

        try {
            final RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
            final RedisSerializer<String> valueSerializer = redisTemplate.getStringSerializer();

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                byte[] rawKey = keySerializer.serialize(key);
                if (rawKey == null) {
                    log.error("Serialized ZSET key is null for key string '{}' ({} {}). Aborting population.",
                            key, entityName, entityId);
                    return null;
                }
                connection.keyCommands().del(rawKey);

                for (LikeDto likeData : likesToCache) {
                    if (likeData.userId() != null && likeData.createdAt() != null) {
                        byte[] rawMember = valueSerializer.serialize(String.valueOf(likeData.userId()));
                        if (rawMember != null) {
                            connection.zSetCommands().zAdd(
                                    rawKey,
                                    (double) likeData.createdAt()
                                            .atZone(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    rawMember
                            );
                        } else {
                            log.warn("Null serialized member for userId {} on {}Id {}. Skipping.",
                                    likeData.userId(), entityName, entityId);
                        }
                    } else {
                        log.warn("Encountered null userId or createdAt in likesToCache for {}Id {}. " +
                                        "Skipping likeData: {}",
                                entityName, entityId, likeData);
                    }
                }
                connection.keyCommands().expire(rawKey, Duration.ofMinutes(likesCacheTtl).getSeconds());
                return null;
            });

            log.info("Successfully populated and set TTL for likes ZSET cache for {} ID: {} with {} likes. Key: {}",
                    entityName, entityId, likesToCache.size(), key);
            return likesToCache.size();

        } catch (DataAccessException e) {
            log.error("Redis access error populating ZSET cache for {} {}. Key: {}. Error: {}",
                    entityName, entityId, key, e.getMessage(), e);
            return 0;
        } catch (Exception e) {
            log.error("Unexpected error populating ZSET cache for {} {}. Key: {}. Error: {}",
                    entityName, entityId, key, e.getMessage(), e);
            return 0;
        }
    }
}
