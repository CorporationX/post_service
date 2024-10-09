package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostRedisService {
    private final PostRedisRepository postRedisRepository;
    private final PostMapper postMapper;

    public List<PostRedis> getAllByIds(Iterable<Long> ids) {
        Iterable<PostRedis> postRedisIterable = postRedisRepository.findAllById(ids);
        return StreamSupport.stream(postRedisIterable.spliterator(), false)
                .toList();
    }

    public void save(Post post) {
        postRedisRepository.save(postMapper.toRedis(post));
    }

    public void updateIfExists(Post updatedPost) {
        if (updatedPost.isPublished()) {
            Optional<PostRedis> old = postRedisRepository.findById(updatedPost.getId());
            if (old.isPresent()) {
                PostRedis postRedis = old.get();
                postRedis.setContent(updatedPost.getContent());
                postRedisRepository.save(postMapper.toRedis(updatedPost));
            }
        }
    }

    public void deleteIfExists(Long id) {
        if (postRedisRepository.existsById(id)) {
            postRedisRepository.deleteById(id);
        }
    }
}