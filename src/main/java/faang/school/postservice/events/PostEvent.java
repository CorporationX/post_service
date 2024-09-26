package faang.school.postservice.events;

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

    private Long postId;

    private Long authorId;

    private List<Long> authorFollowerIds;
}
