package faang.school.postservice.config.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;

@AllArgsConstructor
@Getter
@Setter
public class FeedRedis implements Serializable {
    private Long userId;
    private LinkedHashSet<Long> postIds;
}
