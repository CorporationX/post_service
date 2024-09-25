package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.event.PostCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class KafkaPostConsumer {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.feed.max-size}")
    private int maxFeedSize;

    @KafkaListener(topics = "posts", groupId = "post-group")
    public void listen(PostCreatedEvent event, Acknowledgment ack) {
        Set<Long> postSet;

        List<Long> followerIds = event.getFollowersIds();
        Long postId = event.getPostId();

        for (Long followerId : followerIds) {
            String redisKey = "feed:" + followerId;
            postSet = (Set<Long>) redisTemplate.opsForValue().get(redisKey);
            if (postSet == null) {
                postSet = new LinkedHashSet<>();
            }
            postSet.remove(postId);
            postSet.add(postId);
            if (postSet.size() > maxFeedSize) {
                postSet.remove(postSet.iterator().next());
            }
            redisTemplate.opsForValue().set(redisKey, postSet);
        }
        ack.acknowledge();
    }
}

