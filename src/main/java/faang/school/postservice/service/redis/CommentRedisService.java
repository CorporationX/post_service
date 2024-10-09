package faang.school.postservice.service.redis;

import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentRedisService {
    private final CommentRedisRepository commentRedisRepository;

    public void saveComment(CommentRedis commentRedis, String zsetKey){
        commentRedisRepository.saveComment(commentRedis, zsetKey);
    }

    public void addLike(String commentId){
        commentRedisRepository.addLike(commentId);
    }
}
