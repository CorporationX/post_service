package faang.school.postservice.cache.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class PostCacheImpl implements PostCache {
    private static final String KEY_PATTERN = "posts:%s";
    @Value("${spring.data.redis.cache.posts.ttl}")
    private int ttl;

    private final RedisTemplate<String, PostDto> template;
    private final PostRepository repository;
    private final PostMapper mapper;
    private final ObjectMapper objectMapper;

    private String getKey(Long postId) {
        return String.format(KEY_PATTERN, postId);
    }

    @Override
    public void set(PostDto dto) {
        template.opsForValue().set(getKey(dto.id()), dto, ttl, TimeUnit.SECONDS);
    }

    @Override
    public PostDto get(long id) {
        PostDto postDto = template.opsForValue().get(getKey(id));
        if (postDto == null) {
            postDto = mapper.toPostDto(repository.findByIdOrThrow(id));
            set(postDto);
        }
        return postDto;
    }

    @Override
    public List<PostDto> getAll(List<Long> ids) {
        List<byte[]> keys = ids.stream()
                .map(id -> getKey(id).getBytes(StandardCharsets.UTF_8))
                .toList();

        List<Object> rawResults = template.executePipelined((RedisCallback<Object>) connection -> {
            for (byte[] key : keys) {
                connection.get(key);
            }
            return null;
        });

        List<Long> missing = new ArrayList<>();
        List<PostDto> posts = new ArrayList<>();
        for (int i = 0; i < rawResults.size(); i++) {
            Object raw = rawResults.get(i);
            if (raw != null) {
                PostDto post = (PostDto) raw;
                posts.add(post);
            } else {
                missing.add(ids.get(i));
            }
        }
        if (!missing.isEmpty()) {
            List<PostDto> byIds = mapper.toPostDtoList(repository.getByIds(missing));
            byIds.forEach(this::set);
            posts.addAll(byIds);
            posts.sort(Comparator.comparing(PostDto::createdAt).reversed());
        }

        return posts;
    }
}
