package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostViewEvent {
    private long postId;
}
