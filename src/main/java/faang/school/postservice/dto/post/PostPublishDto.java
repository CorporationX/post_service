package faang.school.postservice.dto.post;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPublishDto implements Serializable {
    private long postId;
    private long authorId;
    private List<Long> subscribersIds;
}
