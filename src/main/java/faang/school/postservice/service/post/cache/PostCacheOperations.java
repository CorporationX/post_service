package faang.school.postservice.service.post.cache;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.service.post.hash.tag.PostHashTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheOperations {
    private static final String POST_ID_PREFIX = "post:";

    private final PostHashTagService postHashTagService;
    private final RedisTemplate<String, PostCacheDto> redisTemplatePost;
    private final ZSetOperations<String, String> zSetOperations;

    public void addPostToCache(PostCacheDto post, List<String> newTags) {
        log.info("Add post to cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        redisTemplatePost.watch(postId);
        newTags.forEach(redisTemplatePost::watch);
        redisTemplatePost.setEnableTransactionSupport(true);
        boolean success = false;
        while (!success) {
            redisTemplatePost.multi();
            log.info("Transaction started");

            redisTemplatePost.opsForValue().set(postId, post);
            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));

            List<Object> result = redisTemplatePost.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplatePost.setEnableTransactionSupport(false);
                log.info("Transaction executed successfully");
            } else {
                redisTemplatePost.discard();
                log.info("Transaction discarded");
            }
        }
        redisTemplatePost.unwatch();
    }

    public void addListOfPostsToCache(List<PostCacheDto> posts) {
        posts.forEach(post -> {
            addPostToCache(post, post.getHashTags());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
//                for (Map.Entry entry: hashTagIds.entrySet()) {
//                    String tag = entry.getKey().toString();
//                    entry.getValue().
//                }
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

    public void deletePostOfCache(PostCacheDto post, List<String> primalTags) {
        log.info("Delete post of cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();

        redisTemplatePost.watch(postId);
        primalTags.forEach(redisTemplatePost::watch);
        boolean success = false;
        while (!success) {
            redisTemplatePost.setEnableTransactionSupport(true);
            redisTemplatePost.multi();

            redisTemplatePost.delete(postId);
            primalTags.forEach(tag -> zSetOperations.remove(tag, postId));

            List<Object> result = redisTemplatePost.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplatePost.setEnableTransactionSupport(false);
            } else {
                redisTemplatePost.discard();
            }
        }
        redisTemplatePost.unwatch();
    }

    public void updatePostOfCache(PostCacheDto post, List<String> primalTags, List<String> updTags) {
        log.info("Update post of cache, post with id: {}", post.getId());
        String postId = POST_ID_PREFIX + post.getId();
        long timestamp = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        List<String> delTags = postHashTagService.getDeletedHashTags(primalTags, updTags);
        List<String> newTags = postHashTagService.getNewHashTags(primalTags, updTags);

        redisTemplatePost.watch(postId);
        delTags.forEach(redisTemplatePost::watch);
        newTags.forEach(redisTemplatePost::watch);
        boolean success = false;
        while (!success) {
            redisTemplatePost.setEnableTransactionSupport(true);
            redisTemplatePost.multi();

            redisTemplatePost.opsForValue().set(postId, post);
            delTags.forEach(tag -> zSetOperations.remove(tag, postId));
            newTags.forEach(tag -> zSetOperations.add(tag, postId, timestamp));

            zSetOperations.range("java", 0, 100);


            List<Object> result = redisTemplatePost.exec();
            if (!result.isEmpty()) {
                success = true;
                redisTemplatePost.setEnableTransactionSupport(false);
            } else {
                redisTemplatePost.discard();
            }
        }
        redisTemplatePost.unwatch();
    }

    public Set<String> findIdsByHashTag(String tag, int start, int end) {
        return zSetOperations.range(tag, start, end);
    }

    public List<PostCacheDto> findAllByIds(List<String> ids) {
        return redisTemplatePost.opsForValue().multiGet(ids);
    }

    public PostCacheDto findPostById(String id) {
        return redisTemplatePost.opsForValue().get(id);
    }

}
