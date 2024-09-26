package faang.school.postservice.service.redis;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.redis.Post;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.postservice.converters.CollectionConverter.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {
    private final PostCacheRepository postRepository;

    public Post findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    }

    public List<Post> findPosts(List<Long> ids) {
        return toList(postRepository.findAllById(ids));
    }

}
