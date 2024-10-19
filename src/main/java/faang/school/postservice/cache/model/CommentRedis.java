package faang.school.postservice.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
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
