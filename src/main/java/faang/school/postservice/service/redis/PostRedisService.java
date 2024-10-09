package faang.school.postservice.service.redis;

import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostRedisService {
    private final PostRedisRepository postRedisRepository;

    public void savePost(PostRedis postRedis) {
        postRedisRepository.savePost(postRedis);
    }

    public void addComment(String postId, String zsetKey) {
        postRedisRepository.addComment(postId, zsetKey);
    }

    public void addLike(String postId){
        postRedisRepository.addLike(postId);
    }

    public void addView(String postId){ //подумать над реализацией
        postRedisRepository.addView(postId);
    }
}
