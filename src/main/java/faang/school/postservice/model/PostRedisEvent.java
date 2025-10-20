package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

import java.util.List;

@RedisHash(value = "postRedisEvent", timeToLive = 60000000)
public class PostRedisEvent {
    @Id
    private Integer followerId;
    private List<PostEventData> postEventData;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Indexed
    public static class PostEventData {
        private Long postId;
    }
}
