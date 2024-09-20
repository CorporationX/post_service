package faang.school.postservice.dto.publishable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEvent {
    private long authorId;
    private long postId;
    private List<Long> subscriberIds;
}
