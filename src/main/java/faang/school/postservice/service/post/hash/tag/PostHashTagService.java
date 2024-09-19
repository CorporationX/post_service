package faang.school.postservice.service.post.hash.tag;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PostHashTagService {
    private static final String HASH_TAG_PATTERN = "#(\\w++)";

//    private final PostRepository postRepository;
    private final JedisPool jedisPool;

    public Post updateHashTags(Post post) {
        List<String> hashTags = parsePostByHashTa(post);
        post.setHashTags(hashTags);
        if (!hashTags.isEmpty()) {

        }
        return post;
    }

    private List<String> parsePostByHashTa(Post post) {
        Pattern pattern = Pattern.compile(HASH_TAG_PATTERN);
        Matcher matcher = pattern.matcher(post.getContent());
        return matcher.results()
                .map(tag -> tag.group(1))
                .toList();
    }

    private void addPostIntoCashByHashTags(Post post) {
        LocalDateTime publishedAt = post.getPublishedAt();
        long timeStamp = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();
        String json = "{\"id\":35,\"title\":\"Article TEST create\",\"text\":\"Text for article 29\",\"rating\":4.1,\"hashTags\":[\"cooking\",\"sport\",\"travelling\",\"java\",\"gradle\"]}";
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.zadd("posts", timeStamp, json);
        }
    }
}
