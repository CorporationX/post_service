package faang.school.postservice.dto.publishable.fornewsfeed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentDeleteEvent {
    private long id;
    private long postId;
}
