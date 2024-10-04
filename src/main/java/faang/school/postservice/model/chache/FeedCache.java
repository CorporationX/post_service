package faang.school.postservice.model.chache;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedCache implements Serializable {
    private long id;
    private LinkedHashSet<Long> postIds;
}
