package faang.school.postservice.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostLikedEvent {
    private Long postID;
    private Long authorID;
}
