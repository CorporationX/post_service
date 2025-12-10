package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Indexed;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@RedisHash(value = "postRedisEvent", timeToLive = 60000000)
public class PostRedisEvent {
    @Id
    private Integer followerId;
    private Set<PostEventData> postEventDataSet = new HashSet<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Indexed
    public static class PostEventData {
        private Long postId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PostRedisEvent that = (PostRedisEvent) o;
        return Objects.equals(followerId, that.followerId) && Objects.equals(postEventDataSet, that.postEventDataSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, postEventDataSet);
    }

}
