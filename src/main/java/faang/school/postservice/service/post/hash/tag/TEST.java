package faang.school.postservice.service.post.hash.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TEST {

    public static void main(String[] args) {
        var updPost = findPostById(9);
        var updTags = updPost.get().getHashTags();
        System.out.println("Primal tags: " + updTags);
        updTags.remove(0);
        updTags.add("newtag");

        postCacheProcess(updPost.get());


    }

    private static final String POST_KEY_PREFIX = "post:";

    private static final JedisPool jedisPool = new JedisPool();
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    public static void postCacheProcess(Post updatedPost) {
        List<String> primalHashTags = getPrimalHashTagsById(updatedPost.getId());
        List<String> updatedHashTags = updatedPost.getHashTags();
        List<String> deletedHashTags = getDeletedHashTags(primalHashTags, updatedHashTags);
        List<String> newHashTags = getNewHashTags(primalHashTags, updatedHashTags);

        System.out.println("DEL: " + deletedHashTags);
        System.out.println("NEW: " + newHashTags);
    }

    private static List<String> getNewHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        List<String> newHashTags = new ArrayList<>(updatedHashTags);
        newHashTags.removeAll(primalHashTags);
        return newHashTags;
    }

    private static List<String> getDeletedHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        List<String> deletedHashTags = new ArrayList<>(primalHashTags);
        deletedHashTags.removeAll(updatedHashTags);
        return deletedHashTags;
    }

    public static Optional<Post> findPostById(long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String primalPostJson = jedis.get(POST_KEY_PREFIX + id);
            if (primalPostJson == null) {
                return Optional.empty();
            }
            return Optional.of(toPost(primalPostJson));
        }
    }

    private static List<String> getPrimalHashTagsById(long id) {
        Optional<Post> postOpt = findPostById(id);
        if (postOpt.isPresent()) {
            return postOpt.get().getHashTags();
        } else {
            return new ArrayList<>();
        }
    }

//    public static void updatePostInCash(List<String> primeHashTags, Post post) {
//        log.info("Update in cash post with id: {}", post.getId());
//        String json = toJson(post);
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.set(POST_KEY_PREFIX + post.getId(), json);
//        }
//    }
//
//    public static void deletePostInCash(Post post) {
//
//    }
//
//    private static String toJson(Post post) {
//        log.info("Parse to json post with id: {}", post.getId());
//        try {
//            return objectMapper.writeValueAsString(post);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
    private static Post toPost(String json) {
        log.info("Parse to Post json: {}", json);
        try {
            return objectMapper.readValue(json, Post.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
