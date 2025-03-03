package faang.school.postservice.service.feed;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.feed.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPostService {

    private final RedisPostRepository redisPostRepository;

    public void save(Post post) {

    }


}
