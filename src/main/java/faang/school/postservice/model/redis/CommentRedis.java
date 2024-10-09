package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRedis implements Comparable<CommentRedis> {
    private Long id;
    private String content;
    private UserRedis author;
    private Long postId;

    @Override
    public int compareTo(CommentRedis other) {
        return other.id.compareTo(this.id);
    }
}
