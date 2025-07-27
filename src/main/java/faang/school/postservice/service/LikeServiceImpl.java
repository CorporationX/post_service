package faang.school.postservice.service;

import faang.school.postservice.client.FeignUserServiceAdapter;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.TotalLikesIsZeroException;
import faang.school.postservice.model.internal.PageWindow;
import faang.school.postservice.model.internal.ResolvedUsers;
import faang.school.postservice.service.redis.like.LikeCacheService;
import faang.school.postservice.service.redis.like.RedisCommentLikeCacheService;
import faang.school.postservice.service.redis.like.RedisPostLikeCacheService;
import faang.school.postservice.service.redis.user.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeServiceInterface {
    private static final int PREFETCH_FACTOR = 5;

    private final FeignUserServiceAdapter userFeignClient;

    private final RedisPostLikeCacheService postLikeCache;
    private final RedisCommentLikeCacheService commentLikeCache;
    private final UserCacheService userCache;

    @Override
    @Transactional
    public Page<UserDto> findLikersByPostId(Long postId, Pageable pageable) {
        log.info("Finding likes for post ID: {} with pageable: {}", postId, pageable);
        if (postId == null) {
            log.warn("postId cannot be null for findLikesByPostId");
            return Page.empty(pageable);
        }
        return findLikesInternal(postId, pageable, postLikeCache);
    }

    @Override
    @Transactional
    public Page<UserDto> findLikersByCommentId(Long commentId, Pageable pageable) {
        log.info("Finding likes for comment ID: {} with pageable: {}", commentId, pageable);
        if (commentId == null) {
            log.warn("commentId cannot be null for findLikesByCommentId");
            return Page.empty(pageable);
        }
        return findLikesInternal(commentId, pageable, commentLikeCache);
    }

    public Page<UserDto> findLikesInternal(Long entityId,
                                           Pageable pageable,
                                           LikeCacheService likeCache) {
        String entityName = likeCache.getEntityName();
        log.info("Starting findLikesInternal for {} ID: {}, page: {}, size: {}",
                entityName, entityId, pageable.getPageNumber(), pageable.getPageSize());

        likeCache.touchKeys(entityId);
        log.debug("Touched cache keys for {} ID: {}", entityName, entityId);

        PageWindow idsWindow = loadUserIds(entityId, pageable, likeCache);
        log.info("Loaded user IDs for {} ID: {}. Found {} IDs for page, total likes: {}.",
                entityName, entityId, idsWindow.userIds().size(), idsWindow.totalLikes());
        if (idsWindow.userIds().isEmpty()) {
            log.info("No user IDs found for the current page for {} ID: {}. Returning empty DTO page.",
                    entityName, entityId);
            return emptyPage(pageable, idsWindow.totalLikes());
        }

        ResolvedUsers resolution = resolveUsers(idsWindow.userIds(), entityId, likeCache.getEntityName());
        log.info("Resolved UserDTOs for {} ID: {}. Resolved {} DTOs. Partial Feign response: {}",
                entityName, entityId, resolution.userDtos().size(), resolution.partial());

        logPartialResultIf(resolution, idsWindow, entityId, likeCache.getEntityName());

        List<UserDto> orderedUsers = orderUsers(idsWindow.userIds(), resolution.userDtos());
        log.info("Final ordered list for {} ID: {} contains {} UserDTOs. Total likes for pagination: {}",
                entityName, entityId, orderedUsers.size(), idsWindow.totalLikes());

        return new PageImpl<>(orderedUsers, pageable, idsWindow.totalLikes());
    }

    private PageWindow loadUserIds(Long entityId,
                                   Pageable pageable,
                                   LikeCacheService likeCache) {
        String entityName = likeCache.getEntityName();
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        log.info("Loading user IDs for {} ID: {}. Offset: {}, Limit: {}", entityName, entityId, offset, limit);

        List<Long> ids = likeCache.getUserIdsPage(entityId, offset, limit);
        if (ids != null && !ids.isEmpty()) {
            log.info("L1 Cache HIT for user IDs for {} ID: {}. Found {} IDs. Total likes from cache: {}",
                    entityName, entityId, ids.size(), likeCache.getTotalLikesCount(entityId));

            return new PageWindow(ids, likeCache.getTotalLikesCount(entityId));
        }
        log.info("L1 Cache MISS for user IDs for {} ID: {}. Attempting DB fallback.",
                entityName, entityId);
        long totalLikes = likeCache.getTotalLikesCount(entityId);
        log.info("Total likes count for {} ID: {} from cache: {}",
                entityName, entityId, totalLikes);

        if (totalLikes == 0) {
            log.warn("Total likes count is 0 for {} ID: {}. Throwing TotalLikesIsZeroException.",
                    entityName, entityId);
            throw new TotalLikesIsZeroException(likeCache.getEntityName(), entityId);
        }
        int prefetch = pageable.getPageSize() * PREFETCH_FACTOR;
        PageRequest windowReq = PageRequest.of(pageable.getPageNumber(), prefetch,
                Sort.by("createdAt").descending());
        log.info("Fetching likes window from DB for {} ID: {}. Page: {}, Prefetch size: {}",
                entityName, entityId, windowReq.getPageNumber(), windowReq.getPageSize());

        List<LikeDto> likesFromDb =
                likeCache.fetchLikesPageFromDb(entityId, windowReq).getContent();
        log.info("Fetched {} likes from DB for {} ID: {} for prefetch window.",
                likesFromDb.size(), entityName, entityId);

        if (likesFromDb.size() != totalLikes || !likesFromDb.isEmpty()) {
            log.info("Updating L1 cache for {} ID: {} with {} likes from DB and total count {}.",
                    entityName, entityId, likesFromDb.size(), totalLikes);

            likeCache.populateLikesCacheFromDb(entityId, likesFromDb, totalLikes);
            ids = likeCache.getUserIdsPage(entityId, offset, limit);

            log.info("Retrying fetch from L1 cache for {} ID: {} after population. Found {} IDs.",
                    entityName, entityId, (ids != null ? ids.size() : 0));
        }
        List<Long> finalIds = Optional.ofNullable(ids).orElse(Collections.emptyList());
        log.debug("Returning PageWindow for {} ID: {}. UserIDs count: {}, TotalLikes: {}",
                entityName, entityId, finalIds.size(), totalLikes);
        return new PageWindow(finalIds, totalLikes);
    }

    private ResolvedUsers resolveUsers(List<Long> userIds,
                                       Long entityId,
                                       String entityName) {
        log.debug("Resolving UserDTOs for {} ID: {}. Requested {} user IDs.",
                entityName, entityId, userIds.size());

        Map<Long, UserDto> result = bulkGetFromUserCache(userIds);
        log.info("L2 UserCache: For {} ID: {}, resolved {} UserDTOs from {} requested IDs.",
                entityName, entityId, result.size(), userIds.size());

        List<Long> misses = userIds.stream()
                .filter(id -> !result.containsKey(id))
                .toList();

        boolean partial = false;
        if (!misses.isEmpty()) {
            log.info("L2 UserCache MISS for {} ID: {}. {} user IDs not found. Fetching via Feign: {}",
                    entityName, entityId, misses.size(), misses);
            try {
                Map<Long, UserDto> fetched = fetchViaFeign(misses, entityId, entityName);
                log.info("FeignClient: For {} ID: {}, fetched {} UserDTOs for {} requested IDs.",
                        entityName, entityId, fetched.size(), misses.size());
                result.putAll(fetched);
                if (!fetched.isEmpty()) {
                    userCache.bulkPut(fetched.values());
                }
                partial = fetched.size() < misses.size();
                if (partial) {
                    log.warn("FeignClient response for {} ID: {} was partial. Requested {}, received {}.",
                            entityName, entityId, misses.size(), fetched.size());
                }
            } catch (Exception e) {
                log.error("Feign error while fetching users for {} ID {} (IDs: {}): {}",
                        entityName, entityId, misses, e.getMessage(), e);
                partial = true;
            }
        } else {
            log.info("L2 UserCache HIT for all {} requested user IDs for {} ID: {}. No Feign call needed.",
                    userIds.size(), entityName, entityId);
        }
        log.debug("Returning ResolveUsers for {} ID: {}. UserDTOs count: {}, Partial: {}",
                entityName, entityId, result.size(), partial);
        return new ResolvedUsers(result, partial);
    }

    private Map<Long, UserDto> bulkGetFromUserCache(List<Long> userIds) {
        log.debug("Attempting bulkGet from UserCache for {} IDs: {}",
                userIds.size(), userIds);
        List<UserDto> fromCache = userCache.bulkGet(userIds, 0, userIds.size());
        log.debug("UserCache raw bulkGet returned {} DTOs for {} requested IDs.",
                fromCache.size(), userIds.size());

        return fromCache.stream()
                .filter(Objects::nonNull)
                .filter(dto -> dto.id() != null)
                .collect(Collectors.toMap(UserDto::id, dto -> dto));
    }


    private Map<Long, UserDto> fetchViaFeign(List<Long> ids,
                                             Long entityId,
                                             String entityName) {
        List<UserDto> fetched = userFeignClient.fetchUserDtosViaFeign(ids, entityName, entityId);
        log.debug("FeignClient raw fetchUserDtosViaFeign returned {} DTOs for {} ID {}.",
                fetched.size(), entityName, entityId);
        return fetched.stream()
                .filter(Objects::nonNull)
                .filter(dto -> dto.id() != null)
                .collect(Collectors.toMap(UserDto::id, dto -> dto));
    }

    private List<UserDto> orderUsers(List<Long> order, Map<Long, UserDto> usersById) {
        log.debug("Ordering {} UserDTOs based on a list of {} IDs.",
                usersById.size(), order.size());
        return order.stream()
                .map(usersById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void logPartialResultIf(ResolvedUsers resolvedUsers,
                                    PageWindow pageWindow,
                                    Long entityId,
                                    String entityName) {
        if (resolvedUsers.partial() || resolvedUsers.userDtos().size() < pageWindow.userIds().size()) {
            log.warn("Partial user list for {} ID {} → expected {}, got {}",
                    entityName, entityId, pageWindow.userIds().size(), resolvedUsers.userDtos().size());
        } else {
            log.debug("User list for {} ID {} is complete. Expected IDs: {}, Got DTOs: {}. Feign was not partial.",
                    entityName, entityId, pageWindow.userIds().size(), resolvedUsers.userDtos().size());
        }
    }

    private PageImpl<UserDto> emptyPage(Pageable pageable, long total) {
        log.info("Returning an empty page of UserDTOs. Pageable: {}, Total elements for pagination: {}",
                pageable, total);
        return new PageImpl<>(Collections.emptyList(), pageable, total);
    }
}
