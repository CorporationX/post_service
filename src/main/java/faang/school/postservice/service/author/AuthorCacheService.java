package faang.school.postservice.service.author;

import faang.school.postservice.dto.author.AuthorDto;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthorCacheService {

    private final RedisTemplate<String, AuthorDto> redisTemplate;

    public void cacheAuthor(AuthorDto authorDto) {
        redisTemplate.opsForValue().set(
                key(authorDto.id()),
                authorDto,
                authorDto.ttl()
        );
    }

    public Optional<AuthorDto> getAuthor(String authorId) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(key(authorId))
        );
    }

    private String key(String authorId) {
        return "author:" + authorId;
    }
}
