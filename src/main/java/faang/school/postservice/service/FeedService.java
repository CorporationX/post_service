package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserContext userContext;
    private final FeedCacheRepository feedCacheRepository;

    private final PostService postService;

    public List<RedisPost> getFeed(Optional<Long> postId){
        // id поста, после которого нужно подгрузить следующую пачку постов фид
        //20 постов из Redis-фида данного пользователя, который запрашивает фид.
        // Текущего пользователя можно получить из userContext

        return  null;
    }


}
