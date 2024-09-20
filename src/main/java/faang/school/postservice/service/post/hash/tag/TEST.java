package faang.school.postservice.service.post.hash.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.resps.Tuple;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TEST {
    private static final String HASH_TAG_PATTERN = "#(\\w++)";

    private static final JedisPool jedisPool = new JedisPool();
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static void main(String[] args) throws JsonProcessingException {
//        var post = buildPost();
//        System.out.println(post);
//        updateHashTags(post);
//        System.out.println(post);

//        String json = objectMapper.writeValueAsString(LocalDateTime.now());
//        System.out.println(json);

        try(Jedis jedis = jedisPool.getResource()) {
            List<String> tuple = jedis.zrange("posts", 0, 0);
            System.out.println(tuple.toString());
            Post post = objectMapper.readValue(tuple.get(0), Post.class);
            System.out.println(post);
//            jedis.zrem("{\"id\":3,\"content\":\"Some post for test #java #jedis#redis #develop\",\"authorId\":null,\"projectId\":null,\"likes\":null,\"comments\":null,\"albums\":null,\"ad\":null,\"resources\":null,\"published\":false,\"publishedAt\":[2024,9,17,11,59,10,594139000],\"scheduledAt\":null,\"deleted\":false,\"createdAt\":null,\"updatedAt\":null,\"hashTags\":[\"java\",\"jedis\",\"redis\",\"develop\"]}");
        }
    }

    private static Post buildPost() {
        return Post
                .builder()
                .id(3L)
                .content("Some post for test #java #jedis#redis #develop")
                .publishedAt(LocalDateTime.now().minusDays(3L))
                .build();
    }

    public static Post updateHashTags(Post post) throws JsonProcessingException {
        List<String> hashTags = parsePostByHashTa(post);
        post.setHashTags(hashTags);
        if (!hashTags.isEmpty()) {
            addPostIntoCashByHashTags(post);
        }
        return post;
    }

    private static List<String> parsePostByHashTa(Post post) {
        Pattern pattern = Pattern.compile(HASH_TAG_PATTERN);
        Matcher matcher = pattern.matcher(post.getContent());
        return matcher.results()
                .map(tag -> tag.group(1))
                .toList();
    }

    private static void addPostIntoCashByHashTags(Post post) throws JsonProcessingException {
        LocalDateTime publishedAt = post.getPublishedAt();
        long timeStamp = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
//        String json = "{\"id\":35,\"title\":\"Article TEST create\",\"text\":\"Text for article 29\",\"rating\":4.1,\"hashTags\":[\"cooking\",\"sport\",\"travelling\",\"java\",\"gradle\"]}";
        String json = objectMapper.writeValueAsString(post);
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.zadd("posts", timeStamp, json);
        }
    }
}
