package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PostHashtagCacheService {
    private final PostRepository postRepository;
    private final Jedis jedis;
    private final ObjectMapper objectMapper;

    @Value("${app.cache-live-time}")
    private int CACHE_LIVE_TIME;

    private List<Post> deserializeCachedData(byte[] cachedData) {
        if (cachedData == null) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(cachedData, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing cached data: " + e.getMessage(), e);
        }
    }

    private void updateCache(String cacheKey, List<Post> cachedPosts) {
        try {
            jedis.setex(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(cachedPosts));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing cached data: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void setPostsIntoCache(Post post) {
        updateOrRemovePostInCache(post, false);
    }

    @Transactional
    public void removePostFromCache(Post post) {
        updateOrRemovePostInCache(post, true);
    }

    @Transactional(readOnly = true)
    public List<Post> getPostsByHashtag(String hashtag) {
        String cacheKey = "postsBy:" + hashtag;
        List<Post> cachedPosts = getPostsFromCache(cacheKey);

        if (!cachedPosts.isEmpty()) {
            return cachedPosts;
        }

        List<Post> posts = postRepository.findPostsByHashtag(turnIntoJson(hashtag));

        if (posts != null && !posts.isEmpty()) {
            try {
                jedis.setex(cacheKey.getBytes(), CACHE_LIVE_TIME, objectMapper.writeValueAsBytes(posts));
                return posts;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error while serializing posts for cache: " + e.getMessage(), e);
            }
        }

        return Collections.emptyList();
    }

    private void updateOrRemovePostInCache(Post post, boolean remove) {
        List<String> hashtags = post.getHashtags();
        for (String hashtag : hashtags) {
            String cacheKey = "postsBy:" + hashtag;
            Transaction transaction = null;

            try {
                jedis.watch(cacheKey);
                transaction = jedis.multi();

                Response<byte[]> cachedDataResponse = transaction.get(cacheKey.getBytes());
                List<Object> execResult = transaction.exec();

                if (execResult == null) {
                    throw new RuntimeException("Transaction cancelled for key: " + cacheKey);
                }

                byte[] cachedData = cachedDataResponse.get();
                List<Post> cachedPosts = deserializeCachedData(cachedData);

                if (remove) {
                    cachedPosts.removeIf(p -> p.getId().equals(post.getId()));
                } else {
                    boolean updated = false;
                    for (int i = 0; i < cachedPosts.size(); i++) {
                        if (cachedPosts.get(i).getId().equals(post.getId())) {
                            cachedPosts.set(i, post);
                            updated = true;
                            break;
                        }
                    }
                    if (!updated) {
                        cachedPosts.add(post);
                    }
                }

                updateCache(cacheKey, cachedPosts);

            } catch (Exception e) {
                if (transaction != null) {
                    try {
                        transaction.discard();
                    } catch (Exception ex) {
                        throw new RuntimeException("Error discarding transaction: " + ex.getMessage(), ex);
                    }
                }
                throw new RuntimeException("Error updating or removing post in cache: " + e.getMessage(), e);
            } finally {
                jedis.unwatch();
            }
        }
    }

    private List<Post> getPostsFromCache(String cacheKey) {
        byte[] cachedData = jedis.get(cacheKey.getBytes());
        if (cachedData == null) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(cachedData, new TypeReference<List<Post>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing from cache: " + e.getMessage(), e);
        }
    }

    private static String turnIntoJson(String hashtag) {
        return "[\"" + hashtag + "\"]";
    }
}