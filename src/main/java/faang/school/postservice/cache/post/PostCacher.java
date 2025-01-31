package faang.school.postservice.cache.post;

import faang.school.postservice.cache.CacheHandler;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.kafka.producer.KafkaSender;
import faang.school.postservice.repository.post.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCacher extends CacheHandler<PostCache> {

    @Value("${spring.kafka.topics.post-create.name:post_create}")
    private String postCreateTopic;
    @Value("${partitions.post-create:1000}")
    private int postCreatePartitions;
    private final Long cacheTtl;
    private final PostCacheRepository postCacheRepository;
    private final KafkaSender kafkaSender;
    private final UserServiceClient userServiceClient;

    @Override
    @Async("cacheExecutor")
    public void cache(PostCache postCache) {
        cacheData(postCache, data -> {
            data.setTtl(cacheTtl);
            postCacheRepository.save(data);
        });
    }
}
