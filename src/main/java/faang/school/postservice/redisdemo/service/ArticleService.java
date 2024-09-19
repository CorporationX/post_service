package faang.school.postservice.redisdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.mapper.ArticleMapper;
import faang.school.postservice.redisdemo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private static final int TTL = 10;

//    @Value("${spring.data.redis.host}")
    private String redisHost = "localhost";

//    @Value("${spring.data.redis.port}")
    private int redisPort = 6379;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ObjectMapper jacksonObjectMapper;
    private final JedisPool jedisPool = new JedisPool(redisHost, redisPort);
    private final Random random = new Random();

    public ArticleDto getArticle(Long id) {
        return articleMapper.toArticleDto(articleRepository.findById(id).orElseThrow());
    }

    public ArticleDto getCachedArticle(Long id) {
        System.out.println("HOST: " + redisHost + ", PORT: " + redisPort);
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "article:%d".formatted(id);
            String raw = jedis.get(key);
            System.out.println("RAW: " + raw);
            if (raw != null) {
                return jacksonObjectMapper.readValue(raw, ArticleDto.class);
            }
            var article = getArticle(id);
            jedis.setex(key, TTL, jacksonObjectMapper.writeValueAsString(article));
            return article;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ArticleDto getRandomArticle() {
        long count = articleRepository.count();
        long articleNum = random.nextLong(1, count);
        return getCachedArticle(articleNum);
    }
}
