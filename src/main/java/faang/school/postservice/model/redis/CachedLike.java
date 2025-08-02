package faang.school.postservice.model.redis;

import lombok.Data;

import java.io.Serializable;

@Data
public class CachedLike implements Serializable {
    private Long id;
    private Long postId;
    private Long commentId;
}
