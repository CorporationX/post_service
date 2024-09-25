package faang.school.postservice.dto.post;


import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Version;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;


import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;


@RedisHash(value = "Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedPostDto implements Serializable {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private long likesCount;
    private Set<CommentDto> comments;
    private long views;

    @Version
    private Long version = 0L;

    public void incrementLikesCount() {
        likesCount += 1;
        version++;
    }

    public void incrementViewsCount(){
        views++;
        version++;
    }

    public void addComment(CommentDto commentDto, int maxSize) {
        if (comments == null) {
            comments = new LinkedHashSet<>();
        }
        if (comments.size() >= maxSize) {
            CommentDto firstComment = comments.iterator().next();
            comments.remove(firstComment);
        }
        comments.add(commentDto);
        version++;
    }
}
