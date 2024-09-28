package faang.school.postservice.event.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEvent {
    private Long postId;
    private Long authorId;
    private List<Long> subscriberIds;
}