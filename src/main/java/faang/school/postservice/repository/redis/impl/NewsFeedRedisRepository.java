package faang.school.postservice.repository.redis.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.redis.NewsFeedRedisEntity;
import faang.school.postservice.repository.redis.AbstractRedisRepository;
import faang.school.postservice.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class NewsFeedRedisRepository extends AbstractRedisRepository<NewsFeedRedisEntity> {

    private static final String PREFIX = "userNewsFeed:{}";

    private final ZSetOperations<String, String> opsSet;

    @Value("${application.redis.news-feed-cache-limit:100}")
    private Long newsFeedLimit;

    public NewsFeedRedisRepository(
        StringRedisTemplate redisTemplate,
        ObjectMapper objectMapper,
        Utils utils,
        @Value("${application.redis.news-feed-time-to-live:100}") long timeToLive
    ) {
        super(redisTemplate, objectMapper, utils, timeToLive);
        this.opsSet = this.redisTemplate.opsForZSet();
    }

    @Override
    protected String add(NewsFeedRedisEntity newsFeedRedisEntity) {
        log.debug("add news feed in redis. newsFeed is: {}", newsFeedRedisEntity);
        String key = getKey(PREFIX, newsFeedRedisEntity.getUserId());
        opsSet.add(key, newsFeedRedisEntity.getPostId(), newsFeedRedisEntity.getTimeStamp());
        opsSet.removeRange(key, 0L, newsFeedLimit * -1 - 1);
        return key;
    }
}
