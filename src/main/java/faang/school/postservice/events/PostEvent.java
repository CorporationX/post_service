package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent extends Event {
    private  Long postId;
    private  Long authorId;
    private  List<Long> postFollowersIds;
    private LocalDateTime publishedAt;
}
