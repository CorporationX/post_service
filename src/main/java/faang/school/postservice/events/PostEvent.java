package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostEvent extends Event {
    private  Long postId;
    private  Long authorId;
    private  List<Long> postFollowersIds;
    private LocalDateTime publishedAt;
}
