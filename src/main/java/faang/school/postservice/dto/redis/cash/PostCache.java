package faang.school.postservice.dto.redis.cash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCache implements Serializable {

    private String id;
    private String content;
    private long authorId;

    private int numElements;
    private List<CommentCache> comments;
    private List<Long> likes;
    private LocalDateTime publishedAt;

    @Version
    private long version;

    public void addComment(CommentCache comment) {
        if (comments.size() >= numElements) {
            System.out.println("comments.size() >= numElements");
            comments.remove(0);
        }
        comments.add(comment);
    }
}
