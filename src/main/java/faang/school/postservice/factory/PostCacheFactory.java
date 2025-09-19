package faang.school.postservice.factory;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCacheFactory {

    private final PostMapper postMapper;

    @Value("${cache.post.ttl-seconds}")
    private int ttl;


    public PostCache from(Post post) {
        PostCache cache = postMapper.toPostCache(post);
        cache.setTtl((long) ttl);
        return cache;
    }
}
