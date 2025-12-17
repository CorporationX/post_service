package faang.school.postservice.dto.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "Post")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RedisPostDto implements Serializable {

        @Id
        private Long id;
        private String content;
        private Long authorId;
        private Long projectId;
        private List<Long> likeIds;
        private List<Long> commentIds;
        private List<Long> albumIds;
        private Long adId;
        private List<Long> resourceIds;

        @TimeToLive
        private Long timeToLive;
}
