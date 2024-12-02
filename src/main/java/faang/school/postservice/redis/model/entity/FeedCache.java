package faang.school.postservice.redis.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedList;

@RedisHash(value = "feeds")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class FeedCache implements Serializable {
    @Id
    private Long id;
    private LinkedList<Long> postIds;
}
