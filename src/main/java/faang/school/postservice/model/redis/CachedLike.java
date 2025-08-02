package faang.school.postservice.model.redis;

import lombok.Data;

@Data
public class CachedLike {
    private Long id;
    private Long postId;
    private Long commentId;
}
