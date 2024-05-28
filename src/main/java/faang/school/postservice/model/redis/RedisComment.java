package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisComment implements Serializable {

    private long id;

    private String content;

    private long authorId;

    private Set<Long> likedUserIds;

    private Long postId;
}
