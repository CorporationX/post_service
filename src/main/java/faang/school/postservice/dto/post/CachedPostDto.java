package faang.school.postservice.dto.post;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@RedisHash("Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CachedPostDto implements Serializable {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private Long viewsQuantity;
    private int likesQuantity;
    private Set<CommentDto> comments;
    @Version
    private Long version = 0L;

    public void setViewsQuantity(Long viewsQuantity) {
        this.viewsQuantity = viewsQuantity;
        version++;
    }

    public void incrementLikesQuantity() {
        likesQuantity += 1;
        version++;
    }

    public void addNewComment(CommentDto commentDto, int maxSize) {
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