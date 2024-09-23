package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheOperations {
    private static final String POST_ID_PREFIX = "post:";

    private final PostHashTagService postHashTagService;
    //    private final RedisTemplate<String, Object> redisTemplatePost;
//    private final ZSetOperations<String, Object> zSetOperations;
    private final RedisTemplate<String, PostCacheDto> redisTemplatePost;
    private final ZSetOperations<String, String> zSetOperations;

    //    public void addListOfPostsToCache(List<PostCacheDto> posts) {
//        log.info("Add to cache list of posts");
//        posts.forEach(post -> System.out.println(post.getId() + " : " + post.getHashTags()));
//        posts.forEach(post -> {
//
//            redisTemplatePost.execute(new SessionCallback<Object>() {
//                @Override
//                public Object execute(RedisOperations operations) throws DataAccessException {
//                    addPostToCache(post, post.getHashTags());
//                    return operations.exec();
//                }
//            });
//
////            addPostToCache(post, post.getHashTags());
////            transactionalMethod(post, post.getHashTags());
//        });
//    }
    // Код Влада:
//    public void transactionalMethod(Object obj) {
//        redisTemplate.execute(new SessionCallback<Object>() {
//            @Override
//            public Object execute(RedisOperations operations) throws DataAccessException {
//                operations.watch(key);
//                operations.multi();
//                // Perform operations using 'operations'
//                operations.opsForValue().set("key", "value");
//                // ...
//                return operations.exec();
//            }
//        });
//    }
//    catch (Exception e) {
//        redisTemplate.discard();
//        throw e;

    public void addListOfPostsToCache(List<PostCacheDto> posts, String tagToFind) {
        log.info("Add to cache list of posts");
        posts.forEach(post -> {
            addPostToCacheByTag(post, post.getHashTags(), tagToFind);
        });
    }

    public void addPostToCacheByTag(PostCacheDto post, List<String> newTags, String tagToFind) {
        log.info("Add post to cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.println(newTags);
        newTags = updateNewTags(newTags, tagToFind);
        System.out.println(newTags);
        if (!newTags.isEmpty()) {
            List<Object> sessionResults = toExecute(post, postId, timestamp, newTags, new ArrayList<>(),
                    false, tagToFind);
        }
    }

    public void addPostToCache(PostCacheDto post, List<String> newTags) {
        log.info("Add post to cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        System.out.println(newTags);
        newTags = updateNewTags(newTags, null);
        System.out.println(newTags);
        if (!newTags.isEmpty()) {
            List<Object> sessionResults = toExecute(post, postId, timestamp, newTags, new ArrayList<>(),
                    false, null);
        }
    }

    public void deletePostOfCache(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post of cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();

        List<Object> sessionResults = toExecute(post, postId, 0, new ArrayList<>(), primalTags,
                true, null);
    }

    public void updatePostOfCache(PostCacheDto post, List<String> primalTags, List<String> updTags) {
        log.info("Update post of cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        List<String> delTags = postHashTagService.getDeletedHashTags(primalTags, updTags);
        List<String> newTags = postHashTagService.getNewHashTags(primalTags, updTags);
        List<Object> sessionResults;
        newTags = updateNewTags(newTags, null);
        if (!newTags.isEmpty() || !delTags.isEmpty()) {
            sessionResults = toExecute(post, postId, timestamp, newTags, delTags, false, null);
        }

//        redisTemplatePost.watch(postId);
//        delTags.forEach(redisTemplatePost::watch);
//        newTags.forEach(redisTemplatePost::watch);
//        boolean success = false;
//        while (!success) {
//            redisTemplatePost.setEnableTransactionSupport(true);
//            redisTemplatePost.multi();
//
//            redisTemplatePost.opsForValue().set(postId, post);
//            delTags.forEach(tag -> zSetOperations.remove(tag, postId));
//            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//
//            List<Object> result = redisTemplatePost.exec();
//            if (!result.isEmpty()) {
//                success = true;
//                redisTemplatePost.setEnableTransactionSupport(false);
//            } else {
//                redisTemplatePost.discard();
//            }
//        }
//        redisTemplatePost.unwatch();
    }

    private List<Object> toExecute(PostCacheDto post, String postId, long timestamp,
                                   List<String> newTags, List<String> delTags,
                                   boolean toDeletePost, String tagToFind) {
        return redisTemplatePost.execute(new SessionCallback<List<Object>>() {
            @Override
            public List<Object> execute(RedisOperations operations) throws DataAccessException {
                List<Object> resultOfExec = new ArrayList<>();
                try {
                    redisTemplatePost.watch(postId);
                    delTags.forEach(redisTemplatePost::watch);
                    newTags.forEach(redisTemplatePost::watch);
                    redisTemplatePost.setEnableTransactionSupport(true);
                    resultOfExec = tryToSaveKeys(post, postId, timestamp, newTags, delTags, toDeletePost, resultOfExec,
                            tagToFind);
                } finally {
                    redisTemplatePost.setEnableTransactionSupport(false);
                    redisTemplatePost.unwatch();
                }
                return resultOfExec;
            }
        });
    }

    private List<String> updateNewTags(List<String> newTags, String tagToFind) {
        return newTags
                .stream()
                .filter(tag -> tag.equals(tagToFind) | Boolean.TRUE.equals(redisTemplatePost.hasKey(tag)))
                .toList();
    }

    private List<Object> tryToSaveKeys(PostCacheDto post, String postId, long timestamp,
                                       List<String> newTags, List<String> delTags,
                                       boolean toDeletePost,
                                       List<Object> resultOfExec,
                                       String tagToFind) {
        System.out.println(delTags);

        boolean success = false;
        while (!success) {
            redisTemplatePost.multi();
            log.info("Transaction started");

//            if (toDeletePost) {
//                redisTemplatePost.delete(postId);
//            } else if(!newTags.isEmpty()) {
//                redisTemplatePost.opsForValue().set(postId, post);
//            }
            if (!newTags.isEmpty()) {
                redisTemplatePost.opsForValue().set(postId, post);
            } else if (toDeletePost) {
                redisTemplatePost.delete(postId);
            }

            delTags.forEach(tag -> zSetOperations.remove(tag, postId));
            newTags.forEach(tag -> {
                zSetOperations.add(tag, postId, timestamp);
            });

            resultOfExec = redisTemplatePost.exec();
            if (!resultOfExec.isEmpty()) {
                success = true;
                log.info("Transaction executed successfully");
            } else {
                redisTemplatePost.discard();
                log.info("Transaction discarded");
            }
        }
        return resultOfExec;
    }

//    public void addPostToCache(PostCacheDto post, List<String> newTags) {
//        log.info("Add post to cache, post with id: {}", post.getId());
//        String postId = POST_ID_PREFIX + post.getId();
//        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//        try {
//            redisTemplatePost.watch(postId);
//            newTags.forEach(redisTemplatePost::watch);
//            redisTemplatePost.setEnableTransactionSupport(true);
//            boolean success = false;
//            while (!success) {
//                redisTemplatePost.multi();
//                log.info("Transaction started");
//
//                redisTemplatePost.opsForValue().set(postId, post);
//                newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//
//                List<Object> result = redisTemplatePost.exec();
//                if (!result.isEmpty()) {
//                    success = true;
//                    log.info("Transaction executed successfully");
//                } else {
//                    redisTemplatePost.discard();
//                    log.info("Transaction discarded");
//                }
//            }
//        } catch (Exception exc) {
//            log.error("{} | Error: {}", exc.getClass(), exc.getMessage());
//        } finally {
//            redisTemplatePost.setEnableTransactionSupport(false);
//            redisTemplatePost.unwatch();
//        }
//    }

//    public void addListOfPostsToCache(List<PostCacheDto> posts) {
//        // Собираем все postId и теги для наблюдения
//        List<String> keysToWatch = new ArrayList<>();
//        for (PostCacheDto post : posts) {
//            String postId = POST_ID_PREFIX + post.getId();
//            keysToWatch.add(postId);
//            keysToWatch.addAll(post.getHashTags());
//        }
//
//        // Наблюдаем за всеми postId и тегами
//        keysToWatch.forEach(redisTemplatePost::watch);
//        redisTemplatePost.setEnableTransactionSupport(true);
//
//        boolean success = false;
//        while (!success) {
//            redisTemplatePost.multi(); // Начало транзакции
//
//            for (PostCacheDto post : posts) {
//                String postId = POST_ID_PREFIX + post.getId();
//                long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//                // Добавляем сам пост
//                redisTemplatePost.opsForValue().set(postId, post);
//
//                // Добавляем пост в отсортированные множества (ZSet) по тегам
//                post.getHashTags().forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//            }
//
//            // Выполняем транзакцию
//            List<Object> result = redisTemplatePost.exec();
//            if (!result.isEmpty()) {
//                success = true;
//                redisTemplatePost.setEnableTransactionSupport(false);
//            } else {
//                redisTemplatePost.discard(); // Откат транзакции при неудаче
//            }
//        }
//
//        redisTemplatePost.unwatch(); // Отменяем наблюдение
//    }

//    public void addListOfPostsToCache(List<PostCacheDto> posts) {
//        if (posts.isEmpty()) {
//            return;
//        }
//
//        // Собираем все postId и теги для наблюдения
//        Set<String> keysToWatch = new HashSet<>();
//        for (PostCacheDto post : posts) {
//            String postId = POST_ID_PREFIX + post.getId();
//            keysToWatch.add(postId);
//            keysToWatch.addAll(post.getHashTags());
//        }
//
//        // Убедиться, что транзакция поддерживается только в нужный момент
//        redisTemplatePost.setEnableTransactionSupport(true);
//
//        // Наблюдаем за всеми postId и тегами
//        try {
//            keysToWatch.forEach(redisTemplatePost::watch);
//
//            boolean success = false;
//            while (!success) {
//                redisTemplatePost.multi(); // Начало транзакции
//                log.info("Transaction started");
//
//                for (PostCacheDto post : posts) {
//                    String postId = POST_ID_PREFIX + post.getId();
//                    long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//                    // Добавляем сам пост
//                    redisTemplatePost.opsForValue().set(postId, post);
//
//                    // Добавляем пост в отсортированные множества (ZSet) по тегам
//                    post.getHashTags().forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//                }
//
//                // Выполняем транзакцию
//                List<Object> result = redisTemplatePost.exec();
//                if (!result.isEmpty()) {
//                    log.info("Transaction executed successfully");
//                    success = true;
//                } else {
//                    log.info("Transaction discarded");
//                    redisTemplatePost.discard(); // Откат транзакции при неудаче
//                }
//            }
//        } finally {
//            redisTemplatePost.setEnableTransactionSupport(false); // Завершение транзакционной поддержки
//            redisTemplatePost.unwatch(); // Отменяем наблюдение
//        }
//    }

//    public void addListOfPostsToCache(List<PostCacheDto> posts) {
//        if (posts.isEmpty()) {
//            return;
//        }
//
//        // Собираем все postId и теги для наблюдения
//        Set<String> postIdsToWatch = new HashSet<>();
//        Set<String> tagsToWatch = new HashSet<>();
//        Map<String, Map<String, Long>> hashTagIds = new HashMap<>();
//        for (PostCacheDto post : posts) {
//            String postId = POST_ID_PREFIX + post.getId();
//            long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//            postIdsToWatch.add(postId);
//            tagsToWatch.addAll(post.getHashTags());
//
//            for (String tag : post.getHashTags()) {
//                var idTimestamp = hashTagIds.computeIfAbsent(tag, k -> new HashMap<String, Long>());
//                idTimestamp.put(postId, timestamp);
//            }
//        }
//
////        Map<String, Map<String, Long>> hashTagIds;
////        for (PostCacheDto post :)
//
//        // Убедиться, что транзакция поддерживается только в нужный момент
//        redisTemplatePost.setEnableTransactionSupport(true);
//
//        // Наблюдаем за всеми postId и тегами
//        try {
//            postIdsToWatch.forEach(redisTemplatePost::watch);
//
//            boolean success = false;
//            while (!success) {
//                redisTemplatePost.multi(); // Начало транзакции
//
//                for (PostCacheDto post : posts) {
//                    String postId = POST_ID_PREFIX + post.getId();
////                    long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//
//                    // Добавляем сам пост
//                    redisTemplatePost.opsForValue().set(postId, post);
//
//                    // Добавляем пост в отсортированные множества (ZSet) по тегам
////                    post.getHashTags().forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//                }
//                // Map<String, Map<String, Long>> hashTagIds
//                hashTagIds.forEach((tag, idsTimestamps) -> {
//                    idsTimestamps.forEach((id, timestamp) -> {
//                        zSetOperations.add(tag, id, timestamp);
//                    });
//                });
////                for (Map.Entry entry: hashTagIds.entrySet()) {
////                    String tag = entry.getKey().toString();
////                    entry.getValue().
////                }
//
//                // Выполняем транзакцию
//                List<Object> result = redisTemplatePost.exec();
//                if (!result.isEmpty()) {
//                    success = true;
//                } else {
//                    redisTemplatePost.discard(); // Откат транзакции при неудаче
//                }
//            }
//        } finally {
//            redisTemplatePost.setEnableTransactionSupport(false); // Завершение транзакционной поддержки
//            redisTemplatePost.unwatch(); // Отменяем наблюдение
//        }
//    }

//    public void deletePostOfCache(PostCacheDto post, List<String> primalTags) {
//        log.info("Delete post of cache, post with id: {}", post.getId());
//        String postId = POST_ID_PREFIX + post.getId();
//
//        redisTemplatePost.watch(postId);
//        primalTags.forEach(redisTemplatePost::watch);
//        boolean success = false;
//        while (!success) {
//            redisTemplatePost.setEnableTransactionSupport(true);
//            redisTemplatePost.multi();
//
//            redisTemplatePost.delete(postId);
//            primalTags.forEach(tag -> zSetOperations.remove(tag, postId));
//
//            List<Object> result = redisTemplatePost.exec();
//            if (!result.isEmpty()) {
//                success = true;
//                redisTemplatePost.setEnableTransactionSupport(false);
//            } else {
//                redisTemplatePost.discard();
//            }
//        }
//        redisTemplatePost.unwatch();
//    }

//    public void updatePostOfCache(PostCacheDto post, List<String> primalTags, List<String> updTags) {
//        log.info("Update post of cache, post with id: {}", post.getId());
//        String postId = POST_ID_PREFIX + post.getId();
//        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
//        List<String> delTags = postHashTagService.getDeletedHashTags(primalTags, updTags);
//        List<String> newTags = postHashTagService.getNewHashTags(primalTags, updTags);
//
//        redisTemplatePost.watch(postId);
//        delTags.forEach(redisTemplatePost::watch);
//        newTags.forEach(redisTemplatePost::watch);
//        boolean success = false;
//        while (!success) {
//            redisTemplatePost.setEnableTransactionSupport(true);
//            redisTemplatePost.multi();
//
//            redisTemplatePost.opsForValue().set(postId, post);
//            delTags.forEach(tag -> zSetOperations.remove(tag, postId));
//            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));
//
//            List<Object> result = redisTemplatePost.exec();
//            if (!result.isEmpty()) {
//                success = true;
//                redisTemplatePost.setEnableTransactionSupport(false);
//            } else {
//                redisTemplatePost.discard();
//            }
//        }
//        redisTemplatePost.unwatch();
//    }

    public Set<String> findIdsByHashTag(String tag, int start, int end) {
        return zSetOperations.reverseRange(tag, start, end);
    }

    public List<PostCacheDto> findAllByIds(List<String> ids) {
        return redisTemplatePost.opsForValue().multiGet(ids);
    }

    public PostCacheDto findPostById(String id) {
        return redisTemplatePost.opsForValue().get(id);
    }

//    public Set<String> findIdsByHashTag(String tag, int start, int end) {
//        return zSetOperations.range(tag, start, end)
//                .stream()
//                .map(id -> (String) id)
//                .collect(Collectors.toSet());
//    }
//
//    public List<PostCacheDto> findAllByIds(List<String> ids) {
//        return redisTemplatePost.opsForValue().multiGet(ids)
//                .stream()
//                .map(post -> (PostCacheDto) post)
//                .toList();
//    }
//
//    public PostCacheDto findPostById(String id) {
//        return (PostCacheDto) redisTemplatePost.opsForValue().get(id);
//    }

}
