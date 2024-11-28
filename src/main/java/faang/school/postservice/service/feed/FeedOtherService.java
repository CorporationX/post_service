package faang.school.postservice.service.feed;

import faang.school.postservice.model.redis.PostCommentsCache;
import faang.school.postservice.model.redis.PostLikesCache;
import faang.school.postservice.model.redis.PostViewsCache;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.redis.RedisPostCommentRepository;
import faang.school.postservice.repository.redis.RedisPostLikeRepository;
import faang.school.postservice.repository.redis.RedisPostViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


// Надо добавить проверку на случай, когда кэш создаётся руками, там макс. id будет null - сделать обработку
// также для кэша лайков добавить likeId для сортировки
@RequiredArgsConstructor
@Service
@Slf4j
public class FeedOtherService {

    // TODO подумать потом как убрать копипаст
    @Value("${feed.comment.default-comment-amount}")
    private final int defaultCommentAmount;

    @Value("${feed.like.default-like-amount}")
    private final int defaultLikeAmount;

    @Value("${feed.post-view.default-post-view-amount}")
    private final int defaultPostViewAmount;

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final RedisPostCommentRepository redisPostCommentRepository;
    private final RedisPostLikeRepository redisPostLikeRepository;
    private final RedisPostViewRepository redisPostViewRepository;

    @Async("kafkaProducerConsumerExecutor") // TODO потом м.б. поменять экзекьютора
    public CompletableFuture<List<Long>> getCommentIds(long postId) {
        Optional<PostCommentsCache> cacheOpt = redisPostCommentRepository.findById(postId);
        PostCommentsCache cache = cacheOpt.orElseGet(() -> PostCommentsCache.builder() // TODO сделать асинхронное заполнение кэша комментов
            .id(postId)
            .commentIds(new LinkedHashSet<>())
            .version(0L)
            .build());
        List<Long> commentIds = new ArrayList<>(cache.getCommentIds());
        int amountFromDb = 0;
        int differenceSize = defaultCommentAmount - commentIds.size();
        if (differenceSize > 0) {
            Long maxCommentId = commentIds.isEmpty() ? 0L : Collections.max(commentIds);
            List<Long> commentIdsFromDb = commentRepository
                .findLastCommentIdsByPostId(postId, maxCommentId, differenceSize);
            amountFromDb = commentIdsFromDb.size();
            commentIds.addAll(commentIdsFromDb);
        }
        log.info("Received {} comments for post {}: {} comments from cache, {} comments from db",
            commentIds.size(), postId, commentIds.size(), amountFromDb
        );

        return CompletableFuture.completedFuture(commentIds);
    }

    @Async("kafkaProducerConsumerExecutor") // TODO потом м.б. поменять экзекьютора
    public CompletableFuture<List<Long>> getLikeIds(long postId) {
        Optional<PostLikesCache> cacheOpt = redisPostLikeRepository.findById(postId);
        PostLikesCache cache = cacheOpt.orElseGet(() -> PostLikesCache.builder() // TODO сделать асинхронное заполнение кэша комментов
            .id(postId)
            .likeIds(new LinkedHashSet<>())
            .version(0L)
            .build());
        List<Long> likeIds = new ArrayList<>(cache.getLikeIds());
        int amountFromDb = 0;
        int differenceSize = defaultLikeAmount - likeIds.size();
        if (differenceSize > 0) {
            Long maxLikeId = likeIds.isEmpty() ? 0L : Collections.max(likeIds);
            List<Long> likeIdsFromDb = likeRepository
                .findLastLikeIdsByPostId(postId, maxLikeId, differenceSize);
            amountFromDb = likeIdsFromDb.size();
            likeIds.addAll(likeIdsFromDb);
        }
        log.info("Received {} likes for post {}: {} likes from cache, {} likes from db",
            likeIds.size(), postId, likeIds.size(), amountFromDb
        );

        return CompletableFuture.completedFuture(likeIds);
    }

    @Async("kafkaProducerConsumerExecutor") // TODO потом м.б. поменять экзекьютора
    public CompletableFuture<List<Long>> getPostViewUserIds(long postId) {
        Optional<PostViewsCache> cacheOpt = redisPostViewRepository.findById(postId);
        PostViewsCache cache = cacheOpt.orElseGet(() -> PostViewsCache.builder() // TODO сделать асинхронное заполнение кэша комментов
            .id(postId)
            .userIds(new LinkedHashSet<>())
            .version(0L)
            .build());
        List<Long> userIds = new ArrayList<>(cache.getUserIds());
        int amountFromDb = 0;
        // тут не ходим в базу, надо делать сущность
        log.info("Received {} likes for post {}: {} likes from cache, {} likes from db",
            userIds.size(), postId, userIds.size(), amountFromDb
        );

        return CompletableFuture.completedFuture(userIds);
    }
    
}
