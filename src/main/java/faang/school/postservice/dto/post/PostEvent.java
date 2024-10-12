package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostEvent {
    private long authorId;
    private long postId;
}
