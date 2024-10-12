package faang.school.postservice.dto.publishable.fornewsfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentEvent {
    private long id;
    private long postId;
    private long authorId;
    private String content;
}
