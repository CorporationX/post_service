package faang.school.postservice.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostEvent {
    private long postId;
    private long authorId;
    private List<Long> subscribersId;
}
