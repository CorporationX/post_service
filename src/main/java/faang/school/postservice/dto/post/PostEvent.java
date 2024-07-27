package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEvent {
    private Long postId;
    private Long authorId;
    private List<Long> subscriberIds;
}