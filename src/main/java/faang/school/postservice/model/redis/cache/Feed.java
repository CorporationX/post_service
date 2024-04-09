package faang.school.postservice.model.redis.cache;

import faang.school.postservice.dto.PostPair;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("feed")
public class Feed implements Serializable {

    @Id
    private long userId;
    private LinkedHashSet<PostPair> posts;

}