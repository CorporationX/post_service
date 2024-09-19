package faang.school.postservice.redisdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.redisdemo.dto.ArticleDto;
import faang.school.postservice.redisdemo.entity.Article;
import faang.school.postservice.redisdemo.mapper.ArticleMapper;
import faang.school.postservice.redisdemo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private static final int TTL = 10;

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final ObjectMapper jacksonObjectMapper;
    private final JedisPool jedisPool = new JedisPool();
    private final Random random = new Random();

    public ArticleDto getArticle(Long id) {
        return articleMapper.toArticleDto(articleRepository.findById(id).orElseThrow());
    }

    public ArticleDto getCachedArticle(Long id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "article:%d".formatted(id);
            String raw = jedis.get(key);
            log.info("RAW: {}", raw);
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

    public ArticleDto createArticle(ArticleDto articleDto){
        var art = articleRepository.save(articleMapper.toEntity(articleDto));
        return articleMapper.toArticleDto(art);
    }

//    public List<ArticleDto> findArticleByHashTag(ArticleDto articleDto) {
//        List<Article> arts = articleRepository.findByHashTags(articleDto.hashTags());
//        return arts.stream()
//                .map(articleMapper::toArticleDto)
//                .toList();
//    }

    public List<ArticleDto> findArticleByHashTag(ArticleDto articleDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonHashTags;
        try {
            jsonHashTags = objectMapper.writeValueAsString(articleDto.hashTags());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert hashTags to JSON", e);
        }
        System.out.println(articleDto);
        System.out.println("JSON: " + jsonHashTags + " #### " + articleDto.hashTags());
        List<Article> arts = articleRepository.findByHashTags(jsonHashTags);
        return arts.stream()
                .map(articleMapper::toArticleDto)
                .toList();
    }
}
