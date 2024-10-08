package faang.school.postservice.model.chache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCache {
    private long id;
    private String content;
    private long authorId;
    private int likeCount;
}
